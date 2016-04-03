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
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import static java.lang.Math.sqrt;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

/**
 * Fast ALS-based factorization of Pilászy, Zibriczky and Tikk.
 * 
 * I. Pilászy, D. Zibriczky and D. Tikk. Fast ALS-based Matrix Factorization
 * for Explicit and Implicit Feedback Datasets. RecSys 2010.
 * 
 * It is a much faster alternative (with slightly worse performance) than that of
 * Hu, Koren and Volinsky.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class PZTFactorizer<U, I> extends ALSFactorizer<U, I> {

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
    public PZTFactorizer(double lambda, DoubleUnaryOperator confidence, int numIter) {
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
    public PZTFactorizer(double lambdaP, double lambdaQ, DoubleUnaryOperator confidence, int numIter) {
        super(numIter);
        this.lambdaP = lambdaP;
        this.lambdaQ = lambdaQ;
        this.confidence = confidence;
    }

    @Override
    public double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data) {
        // TODO: add regularization, unify with HKVFactorizer's error
        
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

    private static <U, I> void set_min(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, DoubleUnaryOperator confidence, double lambda, FastPreferenceData<U, I> data) {
        DoubleMatrix2D gt = getGt(p, q, lambda);

        data.getUidxWithPreferences().parallel().forEach(uidx -> {
            prepareRR1(1, p.viewRow(uidx), gt, q, data.numItems(uidx), data.getUidxPreferences(uidx), confidence, lambda);
        });
    }

    private static DoubleMatrix2D getGt(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, double lambda) {
        final int K = p.columns();

        DenseDoubleMatrix2D A1 = new DenseDoubleMatrix2D(K, K);
        q.zMult(q, A1, 1.0, 0.0, true, false);
        for (int k = 0; k < K; k++) {
            A1.setQuick(k, k, lambda + A1.getQuick(k, k));
        }

        EigenvalueDecomposition eig = new EigenvalueDecomposition(A1);
        DoubleMatrix1D d = eig.getRealEigenvalues();
        DoubleMatrix2D gt = eig.getV();
        for (int k = 0; k < K; k++) {
            double a = sqrt(d.get(k));
            gt.viewColumn(k).assign(x -> a * x);
        }

        return gt;
    }

    private static <O> void prepareRR1(int L, DoubleMatrix1D w, DoubleMatrix2D gt, DoubleMatrix2D q, int N, Stream<? extends IdxPref> prefs, DoubleUnaryOperator confidence, double lambda) {
        int K = (int) w.size();

        double[][] x = new double[K + N][K];
        double[] y = new double[K + N];
        double[] c = new double[K + N];
        for (int k = 0; k < K; k++) {
            gt.viewColumn(k).toArray(x[k]);
            y[k] = 0.0;
            c[k] = 1.0;
        }
        int[] j = {K};
        prefs.forEach(iv -> {
            q.viewRow(iv.v1).toArray(x[j[0]]);
            double Cui = confidence.applyAsDouble(iv.v2);
            y[j[0]] = (Cui * iv.v2) / (Cui - 1);
            c[j[0]] = Cui - 1;
            j[0]++;
        });
        
        doRR1(L, w, x, y, c, lambda);
    }

    private static void doRR1(int L, DoubleMatrix1D w, double[][] x, double[] y, double[] c, double lambda) {
        int N = x.length;
        int K = x[0].length;
        
        double[] e = new double[N];
        for (int i = 0; i < N; i++) {
            double pred = 0.0;
            for (int k = 0; k < K; k++) {
                pred += w.getQuick(k) * x[i][k];
            }
            e[i] = y[i] - pred;
        }

        for (int l = 0; l < L; l++) {
            for (int k = 0; k < K; k++) {
                for (int i = 0; i < N; i++) {
                    e[i] += w.getQuick(k) * x[i][k];
                }
                double a = 0.0;
                double d = 0.0;
                for (int i = 0; i < N; i++) {
                    a += c[i] * x[i][k] * x[i][k];
                    d += c[i] * x[i][k] * e[i];
                }
                w.setQuick(k, d / (lambda + a));
                for (int i = 0; i < N; i++) {
                    e[i] -= w.getQuick(k) * x[i][k];
                }
            }
        }

    }
}
