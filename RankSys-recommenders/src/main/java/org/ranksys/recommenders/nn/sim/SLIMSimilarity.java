package org.ranksys.recommenders.nn.sim;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuples;

import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.IntStream.range;

public class SLIMSimilarity implements Similarity {

    private final FastPreferenceData<?, ?> preferences;
    private final int numIter;
    private final double learnRate;
    private final double lambda;
    private final double alpha;

    public SLIMSimilarity(FastPreferenceData<?, ?> preferences, int numIter, double learnRate, double lambda, double alpha) {
        this.preferences = preferences;
        this.numIter = numIter;
        this.learnRate = learnRate;
        this.lambda = lambda;
        this.alpha = alpha;
    }

    @Override
    public IntToDoubleFunction similarity(int iidx) {
        Int2DoubleMap sims = new Int2DoubleOpenHashMap();
        similarElems(iidx).forEach(t -> sims.put(t.v1, t.v2));

        return sims::get;
    }

    private double[] userErrors(double[] w, double[] r) {
        double[] e = new double[preferences.numUsers()];
        for (int uidx = 0; uidx < e.length; uidx++) {
            e[uidx] = r[uidx];
            IntIterator it = preferences.getUidxIidxs(uidx);
            while (it.hasNext()) {
                e[uidx] -= w[it.nextInt()];
            }
        }

        return e;
    }

    private double error(double[] w, double[] r) {
        return 0.5 * DoubleStream.of(userErrors(w, r)).map(x -> x * x).sum();
    }

    private void updateVector(double[] w, double[] r, int iidx, int[] jidxs, double l1, double l2) {
        double[] userErrors = userErrors(w, r);

        for (int jidx : jidxs) {
            double delta = 0.0;
            IntIterator it = preferences.getIidxUidxs(jidx);
            while (it.hasNext()) {
                delta += userErrors[it.nextInt()];
            }
            delta *= -1;

            if (w[jidx] != 0.0 || delta != 0.0) {
                w[jidx] -= learnRate * (delta + l2 * w[jidx]);
                if (w[jidx] > 0.0) {
                    w[jidx] = max(0, w[jidx] - learnRate * l1);
                } else if (w[jidx] < 0.0) {
                    w[jidx] = min(0, w[jidx] + learnRate * l1);
                }
            }
        }
    }

    @Override
    public Stream<Tuple2id> similarElems(int iidx) {
        double[] w = new double[preferences.numItems()];

        double[] r = new double[preferences.numUsers()];
        preferences.getIidxUidxs(iidx)
                .forEachRemaining(uidx -> r[uidx] = 1.0);

        double l1 = lambda * alpha * Math.log(preferences.numUsers(iidx));
        double l2 = lambda * (1 - alpha) * Math.log(preferences.numUsers(iidx));

//        double e0 = error(w, r);

        double[] userErrors = userErrors(w, r);
        int[] jidxs = IntStream.range(0, preferences.numItems())
                .filter(jidx -> {
                    if (jidx == iidx) {
                        return false;
                    }
                    
                    IntIterator it = preferences.getIidxUidxs(jidx);
                    while (it.hasNext()) {
                        if (userErrors[it.nextInt()] != 0) {
                            return true;
                        }
                    }
                    return false;
                })
                .toArray();

        for (int t = 0; t < numIter; t++) {
            updateVector(w, r, iidx, jidxs, l1, l2);
        }

//        double e1 = error(w, r);

//        System.out.println(iidx + "\t" + e0 + "\t" + e1 + "\t" + DoubleStream.of(w).filter(x -> x > 0).count());

        return range(0, w.length)
                .filter(jidx -> w[jidx] > 0)
                .mapToObj(jidx -> Tuples.tuple(jidx, w[jidx]));
    }

}
