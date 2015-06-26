/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.mf.plsa;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import static cern.jet.math.Functions.identity;
import static cern.jet.math.Functions.mult;
import static cern.jet.math.Functions.plus;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.Factorizer;
import es.uam.eps.ir.ranksys.mf.als.ALSFactorizer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import static java.lang.Math.sqrt;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Probabilistic Latent Semantic Analysis of Hofmann.
 * 
 * T. Hofmann. Latent Semantic Models for Collaborative Filtering. ToIS, Vol 22
 * No. 1, January 2004.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class PLSAFactorizer<U, I> extends Factorizer<U, I> {

    private final int numIter;

    /**
     * Constructor.
     *
     * @param numIter number of expectation-maximization steps
     */
    public PLSAFactorizer(int numIter) {
        this.numIter = numIter;
    }

    @Override
    public double error(Factorization<U, I> factorization, FastPreferenceData<U, I, ?> data) {
        DenseDoubleMatrix2D pu_z = factorization.getUserMatrix();
        DenseDoubleMatrix2D piz = factorization.getItemMatrix();

        double error = data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pU_z = pu_z.viewRow(uidx);
            DoubleMatrix1D pUi = piz.zMult(pU_z, null);
            return data.getUidxPreferences(uidx).mapToDouble(iv -> {
                return -iv.v * pUi.getQuick(iv.idx);
            }).sum();
        }).sum();

        return error;
    }

    @Override
    public Factorization<U, I> factorize(int K, FastPreferenceData<U, I, ?> data) {
        DoubleFunction init = x -> sqrt(1.0 / K) * Math.random();
        Factorization<U, I> factorization = new Factorization<>(data, data, K, init);
        factorize(factorization, data);
        return factorization;
    }

    @Override
    public void factorize(Factorization<U, I> factorization, FastPreferenceData<U, I, ?> data) {
        DenseDoubleMatrix2D pu_z = factorization.getUserMatrix();
        DenseDoubleMatrix2D piz = factorization.getItemMatrix();

        IntSet uidxs = new IntOpenHashSet(data.getUidxWithPreferences().toArray());
        IntStream.range(0, pu_z.rows()).filter(uidx -> !uidxs.contains(uidx)).forEach(uidx -> pu_z.viewRow(uidx).assign(0.0));
        IntSet iidxs = new IntOpenHashSet(data.getIidxWithPreferences().toArray());
        IntStream.range(0, piz.rows()).filter(iidx -> !iidxs.contains(iidx)).forEach(iidx -> piz.viewRow(iidx).assign(0.0));

        PLSAPreferenceData<U, I> plsaData = new PLSAPreferenceData<>(data, pu_z.columns());
        
        // normalize p(z|u)
        plsaData.getUidxWithPreferences().parallel().forEach(uidx -> {
        	final DoubleMatrix1D pU_z = pu_z.viewRow(uidx);
        	pU_z.assign(mult(1 / pU_z.aggregate(plus, identity)));
        });
        
        // normalize p(i|z)
        for (int z = 0; z < piz.columns(); z++) {
        	final DoubleMatrix1D pZ_i = piz.viewColumn(z);
        	pZ_i.assign(mult(1 / pZ_i.aggregate(plus, identity)));
        }

        for (int t = 1; t <= numIter; t++) {
            long time0 = System.nanoTime();

            expectation(pu_z, piz, plsaData);
            maximization(pu_z, piz, plsaData);

            int iter = t;
            long time1 = System.nanoTime() - time0;

            Logger.getLogger(ALSFactorizer.class.getName()).log(Level.INFO, () -> String.format("iteration %3d %.2fs %.6f", iter, time1 / 1_000_000_000.0, error(factorization, data)));
        }
    }

    private void expectation(final DenseDoubleMatrix2D pz_u, final DenseDoubleMatrix2D piz, FastPreferenceData<U, I, double[]> qzData) {
        qzData.getUidxWithPreferences().parallel().forEach(uidx -> {
            qzData.getUidxPreferences(uidx).forEach(iqz -> {
                int iidx = iqz.idx;
                double[] qz = iqz.o;
                for (int z = 0; z < qz.length; z++) {
                    qz[z] = piz.getQuick(iidx, z) * pz_u.getQuick(uidx, z);
                }
                normalizeQz(qz);
            });
        });
    }

    private void maximization(DenseDoubleMatrix2D pu_z, final DenseDoubleMatrix2D piz, final FastPreferenceData<U, I, double[]> qzData) {
        Int2ObjectMap<Lock> lockMap = new Int2ObjectOpenHashMap<>();
        qzData.getIidxWithPreferences().forEach(iidx -> lockMap.put(iidx, new ReentrantLock()));

        pu_z.assign(0.0);
        piz.assign(0.0);

        qzData.getUidxWithPreferences().parallel().forEach(uidx -> {
            final DoubleMatrix1D pz_U = pu_z.viewRow(uidx);

            qzData.getUidxPreferences(uidx).forEach(iqz -> {
                int iidx = iqz.idx;
                double v = iqz.v;
                double[] qz = iqz.o;
                Lock lock = lockMap.get(iidx);

                for (int z = 0; z < qz.length; z++) {
                    double r = qz[z] * v;
                    pz_U.setQuick(z, pz_U.getQuick(z) + r);
                }
                lock.lock();
                try {
                    for (int z = 0; z < qz.length; z++) {
                        double r = qz[z] * v;
                        piz.setQuick(iidx, z, piz.getQuick(iidx, z) + r);
                    }
                } finally {
                    lock.unlock();
                }
            });
        });
        
        // normalize p(z|u)
        qzData.getUidxWithPreferences().parallel().forEach(uidx -> {
        	final DoubleMatrix1D pU_z = pu_z.viewRow(uidx);
        	pU_z.assign(mult(1 / pU_z.aggregate(plus, identity)));
        });
        
        // normalize p(i|z)
        for (int z = 0; z < piz.columns(); z++) {
        	final DoubleMatrix1D pZ_i = piz.viewColumn(z);
        	pZ_i.assign(mult(1 / pZ_i.aggregate(plus, identity)));
        }
    }

    private void normalizeQz(double[] qz) {
        double norm = 0;
        for (int i = 0; i < qz.length; i++) {
            norm += qz[i];
        }
        for (int i = 0; i < qz.length; i++) {
            qz[i] /= norm;
        }
    }

    private static class PLSAPreferenceData<U, I> extends AbstractFastPreferenceData<U, I, double[]> {

        private final FastPreferenceData<U, I, ?> data;
        private final Long2ObjectOpenHashMap<double[]> qz;

        public PLSAPreferenceData(FastPreferenceData<U, I, ?> data, int K) {
            super(data, data);
            this.data = data;
            this.qz = new Long2ObjectOpenHashMap<>();
            data.getUidxWithPreferences().forEach(uidx -> {
                data.getUidxPreferences(uidx).forEach(pref -> {
                    putQz(uidx, pref.idx, new double[K]);
                });
            });

        }

        private double[] getQz(int uidx, int iidx) {
            return qz.get(uidx * data.numItems() + iidx);
        }

        private double[] putQz(int uidx, int iidx, double[] v) {
            return qz.put(uidx * data.numItems() + iidx, v);
        }

        @Override
        public int numUsers(int iidx) {
            return data.numUsers(iidx);
        }

        @Override
        public int numItems(int uidx) {
            return data.numItems(uidx);
        }

        @Override
        public IntStream getUidxWithPreferences() {
            return data.getUidxWithPreferences();
        }

        @Override
        public IntStream getIidxWithPreferences() {
            return data.getIidxWithPreferences();
        }

        @Override
        public Stream<IdxPref<double[]>> getUidxPreferences(int uidx) {
            return data.getUidxPreferences(uidx)
                    .map(pref -> new IdxPref<>(pref.idx, pref.v, getQz(uidx, pref.idx)));
        }

        @Override
        public Stream<IdxPref<double[]>> getIidxPreferences(int iidx) {
            return data.getIidxPreferences(iidx)
                    .map(pref -> new IdxPref<>(pref.idx, pref.v, getQz(pref.idx, iidx)));
        }

        @Override
        public int numPreferences() {
            return data.numPreferences();
        }

    }
}
