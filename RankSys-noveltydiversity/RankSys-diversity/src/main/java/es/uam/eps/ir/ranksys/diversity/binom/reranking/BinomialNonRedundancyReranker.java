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
package es.uam.eps.ir.ranksys.diversity.binom.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialNonRedundancyReranker<U, I, F> extends LambdaReranker<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final BinomialModel<U, I, F> binomialModel;

    public BinomialNonRedundancyReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff) {
        super(lambda, cutoff, true);
        this.featureData = featureData;
        this.binomialModel = binomialModel;
    }

    @Override
    protected BinomialNonRedundancyUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new BinomialNonRedundancyUserReranker(recommendation, maxLength);
    }

    protected class BinomialNonRedundancyUserReranker extends LambdaUserReranker {

        private final BinomialModel<U, I, F>.UserBinomialModel ubm;
        private final Object2IntOpenHashMap<F> featureCount;
        private final Object2DoubleMap<F> patienceNow;
        private final Object2DoubleMap<F> patienceLater;

        public BinomialNonRedundancyUserReranker(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            ubm = binomialModel.getModel(recommendation.getUser());

            featureCount = new Object2IntOpenHashMap<>();
            featureCount.defaultReturnValue(0);

            patienceNow = new Object2DoubleOpenHashMap<>();
            patienceLater = new Object2DoubleOpenHashMap<>();
            ubm.getFeatures().forEach(f -> {
                patienceNow.put(f, ubm.patience(0, f, cutoff));
                patienceLater.put(f, ubm.patience(1, f, cutoff));
            });
        }

        @Override
        protected double nov(IdDouble<I> itemValue) {
            Set<F> itemFeatures = featureData.getItemFeatures(itemValue.id)
                    .map(fv -> fv.id)
                    .collect(Collectors.toCollection(() -> new HashSet<>()));

            double iNonRed = featureCount.keySet().stream()
                    .mapToDouble(f -> {
                        if (itemFeatures.contains(f)) {
                            return patienceLater.getDouble(f);
                        } else {
                            return patienceNow.getDouble(f);
                        }
                    }).reduce((x, y) -> x * y).orElse(1.0);
            int m = featureCount.size() + (int) itemFeatures.stream()
                    .filter(f -> !featureCount.containsKey(f))
                    .count();
            iNonRed = Math.pow(iNonRed, 1 / (double) m);

            return iNonRed;
        }

        @Override
        protected void update(IdDouble<I> bestItemValue) {
            featureData.getItemFeatures(bestItemValue.id)
                    .map(fv -> fv.id)
                    .forEach(f -> {
                        int c = featureCount.addTo(f, 1) + 1;
                        patienceNow.put(f, patienceLater.getDouble(f));
                        patienceLater.put(f, ubm.patience(c + 1, f, cutoff));
                    });
        }

    }

}
