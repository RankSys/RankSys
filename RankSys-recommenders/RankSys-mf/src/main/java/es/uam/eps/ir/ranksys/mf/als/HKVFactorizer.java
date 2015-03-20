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
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.LUDecompositionQuick;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class HKVFactorizer<U, I, O> extends ALSFactorizer<U, I, O> {

    private static final Algebra ALG = new Algebra();
    private final double lambdaP;
    private final double lambdaQ;
    private final DoubleFunction confidence;

    public HKVFactorizer(double lambda, DoubleFunction confidence, int numIter) {
        this(lambda, lambda, confidence, numIter);
    }

    public HKVFactorizer(double lambdaP, double lambdaQ, DoubleFunction confidence, int numIter) {
        super(numIter);
        this.lambdaP = lambdaP;
        this.lambdaQ = lambdaQ;
        this.confidence = confidence;
    }

    @Override
    public double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I, O> data) {
        double error = data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);
            DoubleMatrix1D su = q.zMult(pu, null);
            
            double err1 = data.getUidxPreferences(uidx).mapToDouble(iv -> {
                double rui = iv.v;
                double sui = su.getQuick(iv.idx);
                double cui = confidence.apply(rui);
                return cui * (rui - sui) * (rui - sui) - confidence.apply(0) * sui * sui;
            }).sum();
            
            double err2 = confidence.apply(0) * su.assign(x -> x * x).zSum();
            
            return err1 + err2;
        }).sum();

        return error;
    }

    @Override
    public void set_minP(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, FastPreferenceData<U, I, O> data) {
        set_min(p, q, confidence, lambdaP, data);
    }

    @Override
    public void set_minQ(final DenseDoubleMatrix2D q, final DenseDoubleMatrix2D p, FastPreferenceData<U, I, O> data) {
        set_min(q, p, confidence, lambdaQ, new TransposedPreferenceData<>(data));
    }

    private static <U, I, O> void set_min(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, DoubleFunction confidence, double lambda, FastPreferenceData<U, I, O> data) {
        final int K = p.columns();

        DenseDoubleMatrix2D A1P = new DenseDoubleMatrix2D(K, K);
        q.zMult(q, A1P, 1.0, 0.0, true, false);
        for (int k = 0; k < K; k++) {
            A1P.setQuick(k, k, lambda + A1P.getQuick(k, k));
        }

        DenseDoubleMatrix2D[] A2P = new DenseDoubleMatrix2D[q.rows()];
        data.getIidxWithPreferences().parallel().forEach(iidx -> {
            A2P[iidx] = new DenseDoubleMatrix2D(K, K);
            DoubleMatrix1D qi = q.viewRow(iidx);
            ALG.multOuter(qi, qi, A2P[iidx]);
        });

        data.getUidxWithPreferences().parallel().forEach(uidx -> {
            DoubleMatrix2D A = new DenseDoubleMatrix2D(K, K);
            DoubleMatrix1D b = new DenseDoubleMatrix1D(K);
            A.assign(A1P);
            b.assign(0.0);

            data.getUidxPreferences(uidx).forEach(iv -> {
                int iidx = iv.idx;
                double Rui = iv.v;
                double Cui = confidence.apply(Rui);

                DoubleMatrix1D qi = q.viewRow(iidx);

                A.assign(A2P[iidx], (x, y) -> x + y * (Cui - 1.0));
                b.assign(qi, (x, y) -> x + y * Rui * Cui);
            });
            LUDecompositionQuick lu = new LUDecompositionQuick(0);
            lu.decompose(A);
            lu.solve(b);
            p.viewRow(uidx).assign(b);
        });
    }

}
