/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.mf.als;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.LUDecompositionQuick;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import java.util.function.DoubleUnaryOperator;

/**
 * Implicit matrix factorization of Hu, Koren and Volinsky.
 *
 * Y. Hu, Y. Koren, C. Volinsky. Collaborative filtering for implicit feedback
 * datasets. ICDM 2008.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class HKVFactorizer<U, I> extends ALSFactorizer<U, I> {

    private static final Algebra ALG = new Algebra();
    private final double lambdaP;
    private final double lambdaQ;
    private final DoubleUnaryOperator confidence;

    /**
     * Constructor. Same regularization factor for user and item matrices.
     *
     * @param lambda regularization factor
     * @param confidence confidence function
     * @param numIter number of iterations
     */
    public HKVFactorizer(double lambda, DoubleUnaryOperator confidence, int numIter) {
        this(lambda, lambda, confidence, numIter);
    }

    /**
     * Constructor. Different regularization factors for user and item matrices.
     *
     * @param lambdaP regularization factor for user matrix
     * @param lambdaQ regularization factor for item matrix
     * @param confidence confidence function
     * @param numIter number of iterations
     */
    public HKVFactorizer(double lambdaP, double lambdaQ, DoubleUnaryOperator confidence, int numIter) {
        super(numIter);
        this.lambdaP = lambdaP;
        this.lambdaQ = lambdaQ;
        this.confidence = confidence;
    }

    @Override
    public double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data) {
        // TODO: add regularization
        
        double error = data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);
            DoubleMatrix1D su = q.zMult(pu, null);
            
            double err1 = data.getUidxPreferences(uidx).mapToDouble(iv -> {
                double rui = iv.v2;
                double sui = su.getQuick(iv.v1);
                double cui = confidence.applyAsDouble(rui);
                return cui * (rui - sui) * (rui - sui) - confidence.applyAsDouble(0) * sui * sui;
            }).sum();
            
            double err2 = confidence.applyAsDouble(0) * su.assign(x -> x * x).zSum();
            
            return (err1 + err2) / data.numItems();
        }).sum() / data.numUsers();

        return error;
    }

    @Override
    public void set_minP(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, FastPreferenceData<U, I> data) {
        set_min(p, q, confidence, lambdaP, data);
    }

    @Override
    public void set_minQ(final DenseDoubleMatrix2D q, final DenseDoubleMatrix2D p, FastPreferenceData<U, I> data) {
        set_min(q, p, confidence, lambdaQ, new TransposedPreferenceData<>(data));
    }

    private static <U, I, O> void set_min(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, DoubleUnaryOperator confidence, double lambda, FastPreferenceData<U, I> data) {
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
                int iidx = iv.v1;
                double rui = iv.v2;
                double cui = confidence.applyAsDouble(rui);

                DoubleMatrix1D qi = q.viewRow(iidx);

                A.assign(A2P[iidx], (x, y) -> x + y * (cui - 1.0));
                b.assign(qi, (x, y) -> x + y * rui * cui);
            });
            LUDecompositionQuick lu = new LUDecompositionQuick(0);
            lu.decompose(A);
            lu.solve(b);
            p.viewRow(uidx).assign(b);
        });
    }

}
