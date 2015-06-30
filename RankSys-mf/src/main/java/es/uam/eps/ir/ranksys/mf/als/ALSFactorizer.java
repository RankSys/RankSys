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
package es.uam.eps.ir.ranksys.mf.als;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.Factorizer;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import static java.lang.Math.sqrt;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Generic alternating least-squares factorizer.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class ALSFactorizer<U, I> extends Factorizer<U, I> {

    private static final Logger LOG = Logger.getLogger(ALSFactorizer.class.getName());

    private final int numIter;

    /**
     * Constructor.
     *
     * @param numIter number of least-squares calculations
     */
    public ALSFactorizer(int numIter) {
        this.numIter = numIter;
    }

    @Override
    public double error(Factorization<U, I> factorization, FastPreferenceData<U, I> data) {

        DenseDoubleMatrix2D p = factorization.getUserMatrix();
        DenseDoubleMatrix2D q = factorization.getItemMatrix();

        return error(p, q, data);
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

        DenseDoubleMatrix2D p = factorization.getUserMatrix();
        DenseDoubleMatrix2D q = factorization.getItemMatrix();

        IntSet uidxs = new IntOpenHashSet(data.getUidxWithPreferences().toArray());
        IntStream.range(0, p.rows()).filter(uidx -> !uidxs.contains(uidx)).forEach(uidx -> p.viewRow(uidx).assign(0.0));
        IntSet iidxs = new IntOpenHashSet(data.getIidxWithPreferences().toArray());
        IntStream.range(0, q.rows()).filter(iidx -> !iidxs.contains(iidx)).forEach(iidx -> q.viewRow(iidx).assign(0.0));

        for (int t = 1; t <= numIter; t++) {
            long time0 = System.nanoTime();

            set_minQ(q, p, data);
            set_minP(p, q, data);

            int iter = t;
            long time1 = System.nanoTime() - time0;

            LOG.log(Level.INFO, String.format("iteration n = %3d t = %.2fs", iter, time1 / 1_000_000_000.0));
            LOG.log(Level.FINE, () -> String.format("iteration n = %3d e = %.6f", iter, error(factorization, data)));
        }
    }

    /**
     * Squared loss of two matrices.
     *
     * @param p user matrix
     * @param q item matrix
     * @param data preference data
     * @return squared loss
     */
    protected abstract double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data);

    /**
     * User matrix least-squares step.
     *
     * @param p user matrix
     * @param q item matrix
     * @param data preference data
     */
    protected abstract void set_minP(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data);

    /**
     * Item matrix least-squares step.
     *
     * @param q item matrix
     * @param p user matrix
     * @param data preference data
     */
    protected abstract void set_minQ(DenseDoubleMatrix2D q, DenseDoubleMatrix2D p, FastPreferenceData<U, I> data);
}
