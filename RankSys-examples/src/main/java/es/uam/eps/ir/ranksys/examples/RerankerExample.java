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
package es.uam.eps.ir.ranksys.examples;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData;
import es.uam.eps.ir.ranksys.diversity.distance.reranking.MMR;
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

import static es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser.ddp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.*;

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
        PreferenceData<Long, Long, Void> trainData = SimplePreferenceData.load(trainDataPath, lp, lp, ddp, vp);
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, lp, sp, v -> 1.0);

        Map<String, Supplier<Reranker<Long, Long>>> rerankersMap = new HashMap<>();

        rerankersMap.put("MMR", () -> {
            ItemDistanceModel<Long> dist = new JaccardFeatureItemDistanceModel<>(featureData);
            return new MMR<>(lambda, cutoff, dist);
        });

        rerankersMap.put("XQuAD", () -> {
            IntentModel<Long, Long, String> intentModel = new IntentModel<>(trainData.getUsersWithPreferences(), trainData, featureData);
            return new XQuAD<>(intentModel, lambda, cutoff, true);
        });

        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);

        rerankersMap.forEach((name, reranker) -> {
            System.out.println("Running " + name);
            String recOut = String.format("%s_%s", recIn, name);
            try {
                RecommendationFormat.Writer<Long, Long> writer = format.getWriter(recOut);
                format.getReader(recIn).readAll()
                        .map(rec -> reranker.get().rerankRecommendation(rec, cutoff))
                        .forEach(rerankedRecommendation -> {
                            try {
                                writer.write(rerankedRecommendation);
                            } catch (IOException ex) {
                                throw new UncheckedIOException(ex);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
