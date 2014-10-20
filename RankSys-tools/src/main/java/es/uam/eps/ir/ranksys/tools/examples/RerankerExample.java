/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.tools.examples;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.core.util.parsing.Parsers;
import es.uam.eps.ir.ranksys.diversity.pair.ItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pair.JaccardFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pair.reranking.AvgMMR;
import es.uam.eps.ir.ranksys.diversity.reranking.Reranker;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 *
 * @author saul
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
        Reranker<Long, Long> reranker = new AvgMMR<>(lambda, cutoff, dist);

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
