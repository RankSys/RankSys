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
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData;
import es.uam.eps.ir.ranksys.diversity.distance.reranking.MMR;
import es.uam.eps.ir.ranksys.diversity.intentaware.FeatureIntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.reranking.XQuAD;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.distance.JaccardFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.Reranker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.lp;
import java.io.FileInputStream;
import static org.ranksys.examples.Utils.readFeatureTuples;
import static org.ranksys.examples.Utils.readPreferenceTuples;

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
        PreferenceData<Long, Long> trainData = SimplePreferenceData.load(readPreferenceTuples(new FileInputStream(trainDataPath)));
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(readFeatureTuples(new FileInputStream(featurePath)));

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

        rerankersMap.forEach((name, rerankerSupplier) -> {
            System.out.println("Running " + name);
            String recOut = String.format("%s_%s", recIn, name);
            Reranker<Long, Long> reranker = rerankerSupplier.get();
            try (RecommendationFormat.Writer<Long, Long> writer = format.getWriter(recOut)) {
                format.getReader(recIn).readAll()
                        .map(rec -> reranker.rerankRecommendation(rec, cutoff))
                        .forEach(rerankedRecommendation -> {
                            try {
                                writer.write(rerankedRecommendation);
                            } catch (IOException ex) {
                                throw new UncheckedIOException(ex);
                            }
                        });
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
