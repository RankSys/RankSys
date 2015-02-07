/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel.UserIdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.NDCG.NDCGRelevanceModel.UserNDCGRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rank.LogarithmicDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class NDCG<U, I> extends AbstractRecommendationMetric<U, I> {

    private final NDCGRelevanceModel<U, I> relModel;
    private final int cutoff;
    private final RankingDiscountModel disc;

    public NDCG(int cutoff, NDCGRelevanceModel<U, I> relModel) {
        this.relModel = relModel;
        this.cutoff = cutoff;
        this.disc = new LogarithmicDiscountModel();
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        UserNDCGRelevanceModel userRelModel = (UserNDCGRelevanceModel) relModel.getUserModel(recommendation.getUser());

        double ndcg = 0.0;
        int rank = 0;

        for (IdDouble<I> pair : recommendation.getItems()) {
            ndcg += userRelModel.gain(pair.id) * disc.disc(rank);

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        if (ndcg > 0) {
            ndcg /= idcg(userRelModel);
        }

        return ndcg;
    }

    private double idcg(UserNDCGRelevanceModel relModel) {
        double[] gains = relModel.getGainValues();
        Arrays.sort(gains);
        ArrayUtils.reverse(gains);

        double idcg = 0;
        int n = Math.min(cutoff, gains.length);

        for (int rank = 0; rank < n; rank++) {
            idcg += gains[rank] * disc.disc(rank);
        }

        return idcg;
    }

    public static class NDCGRelevanceModel<U, I> extends IdealRelevanceModel<U, I> {

        private final RecommenderData<U, I, ?> testData;
        private final double threshold;

        public NDCGRelevanceModel(boolean caching, RecommenderData<U, I, ?> testData, double threshold) {
            super(caching, testData.getUsersWithPreferences());
            this.testData = testData;
            this.threshold = threshold;
        }

        @Override
        protected UserIdealRelevanceModel<U, I> get(U user) {
            return new UserNDCGRelevanceModel(user);
        }

        public class UserNDCGRelevanceModel implements UserIdealRelevanceModel<U, I> {

            private final TObjectDoubleMap<I> gainMap;

            public UserNDCGRelevanceModel(U user) {
                this.gainMap = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);

                testData.getUserPreferences(user)
                        .filter(iv -> iv.v >= threshold)
                        .forEach(iv -> gainMap.put(iv.id, Math.pow(2, iv.v - threshold + 1.0) - 1.0));
            }

            @Override
            public Set<I> getRelevantItems() {
                return gainMap.keySet();
            }

            @Override
            public boolean isRelevant(I item) {
                return gainMap.containsKey(item);
            }

            @Override
            public double gain(I item) {
                return gainMap.get(item);
            }

            public double[] getGainValues() {
                return gainMap.values();
            }
        }
    }
}
