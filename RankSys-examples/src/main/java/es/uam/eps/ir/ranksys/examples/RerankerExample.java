/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.examples;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData;
import es.uam.eps.ir.ranksys.diversity.distance.reranking.MMR;
import es.uam.eps.ir.ranksys.diversity.intentaware.FeatureIntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.reranking.XQuAD;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.distance.JaccardFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.Reranker;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jooq.lambda.Unchecked;
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
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(SimpleFeaturesReader.get().read(featurePath, lp, sp));

        Map<String, Supplier<Reranker<Long, Long>>> rerankersMap = new HashMap<>();

        rerankersMap.put("MMR", () -> {
            ItemDistanceModel<Long> dist = new JaccardFeatureItemDistanceModel<>(featureData);
            return new MMR<>(lambda, cutoff, dist);
        });

        rerankersMap.put("XQuAD", () -> {
            IntentModel<Long, Long, String> intentModel = new FeatureIntentModel<>(trainData, featureData);
            return new XQuAD<>(intentModel, lambda, cutoff, true);
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
