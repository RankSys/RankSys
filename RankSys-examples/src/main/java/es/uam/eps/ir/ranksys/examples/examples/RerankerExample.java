/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.examples.examples;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.core.util.parsing.Parsers;
import es.uam.eps.ir.ranksys.diversity.pairwise.ItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pairwise.JaccardFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pairwise.reranking.MMR;
import es.uam.eps.ir.ranksys.diversity.reranking.Reranker;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class RerankerExample {

    public static void main(String[] args) throws Exception {
        String featurePath = args[0];
        String recIn = args[1];
        String recOut = args[2];

        double lambda = 0.5;
        int cutoff = 100;
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, Parsers.lp, Parsers.sp, v -> 1.0);
        ItemDistanceModel<Long> dist = new JaccardFeatureItemDistanceModel<>(featureData);
        Reranker<Long, Long> reranker = new MMR<>(lambda, cutoff, dist);

        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(Parsers.lp, Parsers.lp);

        try (RecommendationFormat.Writer<Long, Long> writer = format.getWriter(recOut)) {
            format.getReader(recIn).readAll().map(reranker::rerankRecommendation).forEach(rerankedRecommendation -> {
                try {
                    writer.write(rerankedRecommendation);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            });
        }
    }
}
