/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.other.metrics;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Subtopic recall metric.
 * 
 * C. X. Zhai, W. W. Cohen, and J. Lafferty. Beyond Independent relevance:
 * methods and evaluation metrics for subtopic retrieval.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class SRecall<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final int cutoff;

    /**
     * relevance model
     */
    protected final RelevanceModel<U, I> relModel;

    /**
     * Constructor.
     *
     * @param featureData feature data
     * @param cutoff maximum length of the recommendation lists to evaluate
     * @param relModel relevance model
     */
    public SRecall(FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        this.featureData = featureData;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());

        Set<F> subtopics = new HashSet<>();
        
        int rank = 0;
        for (Tuple2od<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.v1)) {
                subtopics.addAll(featureData.getItemFeatures(iv.v1)
                        .map(Tuple2::v1)
                        .collect(toList()));
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return subtopics.size() / (double) featureData.numFeatures();
    }

}
