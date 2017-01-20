package org.ranksys.recommenders.mf.icd;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.preference.fast.TransposedPreferenceData;
import org.ranksys.recommenders.mf.Factorization;
import org.ranksys.recommenders.mf.Factorizer;

import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ICDFactorizer<U, I> extends Factorizer<U, I> {

    private static final Logger LOG = Logger.getLogger(ICDFactorizer.class.getName());

    private final Random rnd;
    private final double sdev;
    private final int numIter;
    private final double lambdaP;
    private final double lambdaQ;
    private final DoubleUnaryOperator confidence;

    public ICDFactorizer(Random rnd, double sdev, int numIter, double lambdaP, double lambdaQ, DoubleUnaryOperator confidence) {
        this.rnd = rnd;
        this.sdev = sdev;
        this.numIter = numIter;
        this.lambdaP = lambdaP;
        this.lambdaQ = lambdaQ;
        this.confidence = confidence;
    }

    @Override
    public double error(Factorization<U, I> factorization, FastPreferenceData<U, I> data) {
        // TODO: Unify code with ALS factorisers

        DenseDoubleMatrix2D p = factorization.getUserMatrix();
        DenseDoubleMatrix2D q = factorization.getItemMatrix();
        double c0 = confidence.applyAsDouble(0);

        return data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);
            DoubleMatrix1D su = q.zMult(pu, null);

            double err1 = data.getUidxPreferences(uidx).mapToDouble(iv -> {
                double rui = iv.v2;
                double sui = su.getQuick(iv.v1);

                return confidence.applyAsDouble(rui) * (rui - sui) * (rui - sui) - c0 * sui * sui;
            }).sum();

            double err2 = c0 * su.assign(x -> x * x).zSum();

            return (err1 + err2) / data.numItems();
        }).sum() / data.numUsers();
    }

    @Override
    public Factorization<U, I> factorize(int K, FastPreferenceData<U, I> data) {
        DoubleFunction init = x -> rnd.nextGaussian() * sdev;
        Factorization<U, I> factorization = new Factorization<>(data, data, K, init);
        factorize(factorization, data);

        return factorization;
    }

    @Override
    public void factorize(Factorization<U, I> factorization, FastPreferenceData<U, I> data) {
        DenseDoubleMatrix2D p = factorization.getUserMatrix();
        DenseDoubleMatrix2D q = factorization.getItemMatrix();
        int K = p.columns();

        IntSet uidxs = new IntOpenHashSet(data.getUidxWithPreferences().toArray());
        IntStream.range(0, p.rows()).filter(uidx -> !uidxs.contains(uidx)).forEach(uidx -> p.viewRow(uidx).assign(0.0));
        IntSet iidxs = new IntOpenHashSet(data.getIidxWithPreferences().toArray());
        IntStream.range(0, q.rows()).filter(iidx -> !iidxs.contains(iidx)).forEach(iidx -> q.viewRow(iidx).assign(0.0));

        for (int t = 1; t <= numIter; t++) {
            long time0 = System.nanoTime();

            for (int k = 0; k < K; k++) {
                descentP_k(k, p, q, data);
                descentQ_k(k, q, p, data);
            }

            int iter = t;
            long time1 = System.nanoTime() - time0;

            LOG.log(Level.INFO, String.format("iteration n = %3d t = %.2fs", iter, time1 / 1_000_000_000.0));
            LOG.log(Level.FINE, () -> String.format("iteration n = %3d e = %.6f", iter, error(factorization, data)));
        }
    }

    private void descentP_k(int k, DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data) {
        descentk(k, p, q, confidence, lambdaP, data);
    }


    private void descentQ_k(int k, DenseDoubleMatrix2D q, DenseDoubleMatrix2D p, FastPreferenceData<U, I> data) {
        descentk(k, q, p, confidence, lambdaQ, new TransposedPreferenceData<>(data));
    }


    private static <U, I> void descentk(int k, DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, DoubleUnaryOperator confidence, double lambda, FastPreferenceData<U, I> data) {
        int K = p.columns();
        double c0 = confidence.applyAsDouble(0);

        DenseDoubleMatrix1D Ji = new DenseDoubleMatrix1D(K);
        for (int k2 = 0; k2 < K; k2++) {
            Ji.setQuick(k2, q.viewColumn(k).zDotProduct(q.viewColumn(k2)));
        }

        data.getUidxWithPreferences().parallel().forEach(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);
            double puk = pu.getQuick(k);

            double[] dL_ddL = {0.0, 0.0};
            data.getUidxPreferences(uidx).forEach(pref -> {
                DoubleMatrix1D qi = q.viewRow(pref.v1);
                double qik = qi.getQuick(k);
                double rui = pref.v2;
                double cui = confidence.applyAsDouble(rui);
                double sui = pu.zDotProduct(qi);

                // rescaling
                rui *= cui / (cui - c0);
                cui -= c0;

                dL_ddL[0] += cui * (sui - rui) * qik;
                dL_ddL[1] += cui * qik * qik;
            });

            double dL = dL_ddL[0] + lambda * puk;
            double ddL = dL_ddL[1] + lambda;
            double dR = Ji.zDotProduct(pu);
            double ddR = Ji.getQuick(k);

            pu.setQuick(k, puk - (dL + c0 * dR) / (ddL + c0 * ddR));
        });
    }


}
