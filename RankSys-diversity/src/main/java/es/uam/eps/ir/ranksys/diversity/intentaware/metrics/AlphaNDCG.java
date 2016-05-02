/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware.metrics;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.model.UserModel;
import es.uam.eps.ir.ranksys.core.model.UserModel.Model;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rank.LogarithmicDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel.UserIdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashSet;
import java.util.Set;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * alpha-nDCG metric.
 *
 * C.L. Clarke, M. Kolla, G.V. Cormack, O. Vechtomova, A. Ashkan, S. Büttcher and I. MacKinnon. Novelty and diversity in Information Retrieval evauation. SIGIR 2008.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class AlphaNDCG<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final double alpha;
    private final IdealRelevanceModel<U, I> relModel;
    private final FeatureData<I, F, ?> featureData;
    private final AlphaNDCGIdeal idcg;
    private final RankingDiscountModel disc = new LogarithmicDiscountModel();

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendations lists to evaluate
     * @param alpha tolerance to redundancy parameter
     * @param featureData feature data
     * @param relModel relevance model
     */
    public AlphaNDCG(int cutoff, double alpha, FeatureData<I, F, ?> featureData, BinaryRelevanceModel<U, I> relModel) {
        super();
        this.cutoff = cutoff;
        this.alpha = alpha;
        this.relModel = relModel;
        this.featureData = featureData;

        this.idcg = new AlphaNDCGIdeal();
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        UserRelevanceModel<U, I> urm = relModel.getModel(recommendation.getUser());

        double ndcg = 0.0;
        int rank = 0;
        Object2IntOpenHashMap<F> redundancy = new Object2IntOpenHashMap<>();
        redundancy.defaultReturnValue(0);

        for (Tuple2od<I> pair : recommendation.getItems()) {
            if (urm.isRelevant(pair.v1)) {
                double gain = featureData.getItemFeatures(pair.v1).sequential()
                        .map(Tuple2::v1)
                        .mapToDouble(f -> {
                            int r = redundancy.addTo(f, 1);
                            return Math.pow(1 - alpha, r);
                        }).sum();
                ndcg += gain * disc.disc(rank);
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        if (ndcg > 0) {
            ndcg /= idcg.getModel(recommendation.getUser()).ideal;
        }

        return ndcg;
    }

    private double idcg(UserIdealRelevanceModel<U, I> urm) {
        double ideal = 0;

        Object2IntOpenHashMap<F> redundancy = new Object2IntOpenHashMap<>();
        redundancy.defaultReturnValue(0);
        Set<I> candidates = new HashSet<>(urm.getRelevantItems());
        int rank = 0;

        while (rank <= cutoff && !candidates.isEmpty()) {
            I bi = null;
            double bg = Double.NEGATIVE_INFINITY;
            for (I i : candidates) {
                double gain = featureData.getItemFeatures(i)
                        .map(Tuple2::v1)
                        .mapToDouble(f -> {
                            return Math.pow(1 - alpha, redundancy.getInt(f));
                        }).sum();
                if (gain > bg) {
                    bg = gain;
                    bi = i;
                }
            }
            candidates.remove(bi);
            featureData.getItemFeatures(bi).sequential()
                    .map(Tuple2::v1)
                    .forEach(f -> redundancy.addTo(f, 1));
            ideal += bg * disc.disc(rank);
            rank++;
        }

        return ideal;
    }

    private class AlphaNDCGIdeal extends UserModel<U> {

        public AlphaNDCGIdeal() {
            super(relModel);
        }

        @Override
        protected UserAlphaNDCGIdeal get(U u) {
            return new UserAlphaNDCGIdeal(idcg(relModel.getModel(u)));
        }

        @SuppressWarnings("unchecked")
        @Override
        public UserAlphaNDCGIdeal getModel(U u) {
            return (UserAlphaNDCGIdeal) super.getModel(u);
        }

        public class UserAlphaNDCGIdeal implements Model<U> {

            public final double ideal;

            public UserAlphaNDCGIdeal(double ideal) {
                this.ideal = ideal;
            }

        }
    }
}
