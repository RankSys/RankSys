/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
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
 * T. Hofmann. Latent Semantic Models for Collaborative Filtering. ToIS, Vol 22 No. 1, January 2004.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class PLSAFactorizer<U, I> extends Factorizer<U, I> {

    private static final Logger LOG = Logger.getLogger(PLSAFactorizer.class.getName());

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
    public double error(Factorization<U, I> factorization, FastPreferenceData<U, I> data) {
        DenseDoubleMatrix2D pu_z = factorization.getUserMatrix();
        DenseDoubleMatrix2D piz = factorization.getItemMatrix();

        double error = data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pU_z = pu_z.viewRow(uidx);
            DoubleMatrix1D pUi = piz.zMult(pU_z, null);
            return data.getUidxPreferences(uidx).mapToDouble(iv -> {
                return -iv.v2 * pUi.getQuick(iv.v1);
            }).sum();
        }).sum();

        return error;
    }

    @Override
    public Factorization<U, I> factorize(int K, FastPreferenceData<U, I> data) {
        DoubleFunction init = x -> sqrt(1.0 / K) * Math.random();
        Factorization<U, I> factorization = new Factorization<>(data, data, K, init);
        factorize(factorization, data);
        return factorization;
    }

    @Override
    public void factorize(Factorization<U, I> factorization, FastPreferenceData<U, I> data) {
        DenseDoubleMatrix2D pu_z = factorization.getUserMatrix();
        DenseDoubleMatrix2D piz = factorization.getItemMatrix();

        IntSet uidxs = new IntOpenHashSet(data.getUidxWithPreferences().toArray());
        IntStream.range(0, pu_z.rows()).filter(uidx -> !uidxs.contains(uidx)).forEach(uidx -> pu_z.viewRow(uidx).assign(0.0));
        IntSet iidxs = new IntOpenHashSet(data.getIidxWithPreferences().toArray());
        IntStream.range(0, piz.rows()).filter(iidx -> !iidxs.contains(iidx)).forEach(iidx -> piz.viewRow(iidx).assign(0.0));

        PLSAPreferenceData<U, I> plsaData = new PLSAPreferenceData<>(data, pu_z.columns());

        for (int z = 0; z < pu_z.columns(); z++) {
            final DoubleMatrix1D pu_Z = pu_z.viewColumn(z);
            pu_Z.assign(mult(1 / pu_Z.aggregate(plus, identity)));
        }
        piz.assign(mult(1 / piz.aggregate(plus, identity)));

        for (int t = 1; t <= numIter; t++) {
            long time0 = System.nanoTime();

            expectation(pu_z, piz, plsaData);
            maximization(pu_z, piz, plsaData);

            int iter = t;
            long time1 = System.nanoTime() - time0;

            LOG.log(Level.INFO, String.format("iteration n = %3d t = %.2fs", iter, time1 / 1_000_000_000.0));
            LOG.log(Level.FINE, () -> String.format("iteration n = %3d e = %.6f", iter, error(factorization, data)));
        }
    }

    private void expectation(final DenseDoubleMatrix2D pz_u, final DenseDoubleMatrix2D piz, PLSAPreferenceData<U, I> qzData) {
        qzData.getUidxWithPreferences().parallel().forEach(uidx -> {
            qzData.getUidxPreferences(uidx).forEach(iqz -> {
                int iidx = iqz.v1;
                double[] qz = ((PLSAPreferenceData.PLSAIdxPref) iqz).qz;
                for (int z = 0; z < qz.length; z++) {
                    qz[z] = piz.getQuick(iidx, z) * pz_u.getQuick(uidx, z);
                }
                normalizeQz(qz);
            });
        });
    }

    private void maximization(DenseDoubleMatrix2D pu_z, final DenseDoubleMatrix2D piz, final PLSAPreferenceData<U, I> qzData) {
        Int2ObjectMap<Lock> lockMap = new Int2ObjectOpenHashMap<>();
        qzData.getIidxWithPreferences().forEach(iidx -> lockMap.put(iidx, new ReentrantLock()));

        pu_z.assign(0.0);
        piz.assign(0.0);

        qzData.getUidxWithPreferences().parallel().forEach(uidx -> {
            final DoubleMatrix1D pz_U = pu_z.viewRow(uidx);

            qzData.getUidxPreferences(uidx).forEach(iqz -> {
                int iidx = iqz.v1;
                double v = iqz.v2;
                double[] qz = ((PLSAPreferenceData.PLSAIdxPref) iqz).qz;
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

        for (int z = 0; z < pu_z.columns(); z++) {
            final DoubleMatrix1D pZ_u = pu_z.viewColumn(z);
            pZ_u.assign(mult(1 / pZ_u.aggregate(plus, identity)));
        }
        piz.assign(mult(1 / piz.aggregate(plus, identity)));
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

    private static class PLSAPreferenceData<U, I> extends AbstractFastPreferenceData<U, I> {

        private final FastPreferenceData<U, I> data;
        private final Long2ObjectOpenHashMap<double[]> qz;

        public PLSAPreferenceData(FastPreferenceData<U, I> data, int K) {
            super(data, data);
            this.data = data;
            this.qz = new Long2ObjectOpenHashMap<>();
            data.getUidxWithPreferences().forEach(uidx -> {
                data.getUidxPreferences(uidx).forEach(pref -> {
                    putQz(uidx, pref.v1, new double[K]);
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
        public Stream<IdxPref> getUidxPreferences(int uidx) {
            return data.getUidxPreferences(uidx)
                    .map(pref -> new PLSAIdxPref(pref.v1, pref.v2, getQz(uidx, pref.v1)));
        }

        @Override
        public Stream<IdxPref> getIidxPreferences(int iidx) {
            return data.getIidxPreferences(iidx)
                    .map(pref -> new PLSAIdxPref(pref.v1, pref.v2, getQz(pref.v1, iidx)));
        }

        @Override
        public IntIterator getUidxIidxs(int uidx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoubleIterator getUidxVs(int uidx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntIterator getIidxUidxs(int iidx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoubleIterator getIidxVs(int iidx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean useIteratorsPreferentially() {
            return false;
        }

        @Override
        public int numPreferences() {
            return data.numPreferences();
        }

        public class PLSAIdxPref extends IdxPref {

            public double[] qz;

            public PLSAIdxPref(int idx, double value, double[] qz) {
                super(idx, value);
                this.qz = qz;
            }

        }
    }
}
