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
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import es.uam.eps.ir.ranksys.fast.IdxPref;
import es.uam.eps.ir.ranksys.fast.data.FastRecommenderData;
import es.uam.eps.ir.ranksys.fast.data.TransposedRecommenderData;
import static java.lang.Math.sqrt;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class PZTFactorizer<U, I, O> extends ALSFactorizer<U, I, O> {

    private final double lambdaP;
    private final double lambdaQ;
    private final DoubleFunction confidence;

    public PZTFactorizer(double lambda, DoubleFunction confidence, int numIter) {
        this(lambda, lambda, confidence, numIter);
    }

    public PZTFactorizer(double lambdaP, double lambdaQ, DoubleFunction confidence, int numIter) {
        super(numIter);
        this.lambdaP = lambdaP;
        this.lambdaQ = lambdaQ;
        this.confidence = confidence;
    }

    @Override
    public double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastRecommenderData<U, I, O> data) {
        double error = data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);
            DoubleMatrix1D su = q.zMult(pu, null);
            
            double err1 = data.getUidxPreferences(uidx).mapToDouble(iv -> {
                double rui = iv.v;
                double sui = su.getQuick(iv.idx);
                double cui = confidence.apply(rui);
                return cui * (rui - sui) * (rui - sui) - confidence.apply(0) * sui * sui;
            }).sum();
            
            double err2 = confidence.apply(0) * su.zSum();
            
            return err1 + err2;
        }).sum();

        return error;
    }

    @Override
    public void set_minP(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, FastRecommenderData<U, I, O> data) {
        set_min(p, q, confidence, lambdaP, data);
    }

    @Override
    public void set_minQ(final DenseDoubleMatrix2D q, final DenseDoubleMatrix2D p, FastRecommenderData<U, I, O> data) {
        set_min(q, p, confidence, lambdaQ, new TransposedRecommenderData<>(data));
    }

    private static <U, I, O> void set_min(final DenseDoubleMatrix2D p, final DenseDoubleMatrix2D q, DoubleFunction confidence, double lambda, FastRecommenderData<U, I, O> data) {
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

    private static <O> void prepareRR1(int L, DoubleMatrix1D w, DoubleMatrix2D gt, DoubleMatrix2D q, int N, Stream<IdxPref<O>> prefs, DoubleFunction confidence, double lambda) {
        int K = (int) w.size();

        double[][] x = new double[K + N][K];
        double[] y = new double[K + N];
        double[] c = new double[K + N];
        int[] j = new int[1];
        for (int k = 0; k < K; k++) {
            gt.viewColumn(k).toArray(x[j[0]]);
            y[j[0]] = 0.0;
            c[j[0]] = 1.0;
            j[0]++;
        }
        prefs.forEach(iv -> {
            q.viewRow(iv.idx).toArray(x[j[0]]);
            double Cui = confidence.apply(iv.v);
            y[j[0]] = (Cui * iv.v) / (Cui - 1);
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
