/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.examples;

import org.ranksys.novdiv.intentaware.ScoresAspectModel;
import org.ranksys.novdiv.intentaware.FeatureIntentModel;
import org.ranksys.novdiv.intentaware.AspectModel;
import org.ranksys.novdiv.intentaware.IntentModel;
import org.ranksys.novdiv.intentaware.ScoresRelevanceAspectModel;
import org.ranksys.core.feature.item.ItemFeatureData;
import org.ranksys.core.feature.item.SimpleItemFeatureData;
import org.ranksys.novdiv.distance.reranking.MMR;
import org.ranksys.novdiv.intentaware.reranking.AlphaXQuAD;
import org.ranksys.novdiv.intentaware.reranking.XQuAD;
import org.ranksys.novdiv.distance.ItemDistanceModel;
import org.ranksys.novdiv.distance.JaccardFeatureItemDistanceModel;
import org.ranksys.novdiv.reranking.Reranker;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jooq.lambda.Unchecked;
import org.ranksys.core.preference.PreferenceData;
import org.ranksys.core.preference.SimplePreferenceData;
import org.ranksys.formats.feature.SimpleFeaturesReader;
import static org.ranksys.formats.parsing.Parsers.lp;
import static org.ranksys.formats.parsing.Parsers.sp;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import org.ranksys.formats.rec.RecommendationFormat;
import org.ranksys.formats.rec.SimpleRecommendationFormat;

/**
 * Example main of re-rankers.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class RerankerExample {

    public static void main(String[] args) throws Exception {
        String trainDataPath = args[0];
        String featurePath = args[1];
        String recIn = args[2];

        double lambda = 0.5;
        int cutoff = 100;
        PreferenceData<Long, Long> trainData = SimplePreferenceData.load(SimpleRatingPreferencesReader.get().read(trainDataPath, lp, lp));
        ItemFeatureData<Long, String, Double> featureData = SimpleItemFeatureData.load(SimpleFeaturesReader.get().read(featurePath, lp, sp));

        Map<String, Supplier<Reranker<Long, Long>>> rerankersMap = new HashMap<>();

        rerankersMap.put("MMR", () -> {
            ItemDistanceModel<Long> dist = new JaccardFeatureItemDistanceModel<>(featureData);
            return new MMR<>(lambda, cutoff, dist);
        });

        rerankersMap.put("xQuAD", () -> {
            IntentModel<Long, Long, String> intentModel = new FeatureIntentModel<>(trainData, featureData);
            AspectModel<Long, Long, String> aspectModel = new ScoresAspectModel<>(intentModel);
            return new XQuAD<>(aspectModel, lambda, cutoff, true);
        });

        rerankersMap.put("RxQuAD", () -> {
            double alpha = 0.5;
            IntentModel<Long, Long, String> intentModel = new FeatureIntentModel<>(trainData, featureData);
            AspectModel<Long, Long, String> aspectModel = new ScoresRelevanceAspectModel<>(intentModel);
            return new AlphaXQuAD<>(aspectModel, alpha, lambda, cutoff, true);
        });

        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);

        rerankersMap.forEach(Unchecked.biConsumer((name, rerankerSupplier) -> {
            System.out.println("Running " + name);
            String recOut = String.format("%s_%s", recIn, name);
            Reranker<Long, Long> reranker = rerankerSupplier.get();
            try (RecommendationFormat.Writer<Long, Long> writer = format.getWriter(recOut)) {
                format.getReader(recIn).readAll()
                        .map(rec -> reranker.rerankRecommendation(rec, cutoff))
                        .forEach(Unchecked.consumer(writer::write));
            }
        }));
    }
}
