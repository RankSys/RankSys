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

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import es.uam.eps.ir.ranksys.fast.data.FastRecommenderData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.Factorizer;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class ALSFactorizer<U, I, O> extends Factorizer<U, I, O> {

    private final int numIter;

    public ALSFactorizer(int numIter) {
        this.numIter = numIter;
    }

    @Override
    public double error(Factorization<U, I> factorization, FastRecommenderData<U, I, O> data) {

        DenseDoubleMatrix2D p = factorization.getUserMatrix();
        DenseDoubleMatrix2D q = factorization.getItemMatrix();

        return error(p, q, data);
    }

    @Override
    public void factorize(Factorization<U, I> factorization, FastRecommenderData<U, I, O> data) {

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
            
            Logger.getLogger(ALSFactorizer.class.getName()).log(Level.INFO, () -> String.format("iteration %3d %.2fs %.6f", iter, time1 / 1_000_000_000.0, error(factorization, data)));
        }
    }

    protected abstract double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastRecommenderData<U, I, O> data);

    protected abstract void set_minP(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastRecommenderData<U, I, O> data);

    protected abstract void set_minQ(DenseDoubleMatrix2D q, DenseDoubleMatrix2D p, FastRecommenderData<U, I, O> data);
}
