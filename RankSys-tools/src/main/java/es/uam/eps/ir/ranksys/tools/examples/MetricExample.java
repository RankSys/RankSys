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
import es.uam.eps.ir.ranksys.diversity.pair.metrics.EILD;
import es.uam.eps.ir.ranksys.metrics.AverageRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;

/**
 *
 * @author saul
 */
public class MetricExample {

    public static void main(String[] args) throws Exception {
        String featurePath = args[0];
        int numUsers = Integer.parseInt(args[1]);
        String recIn = args[2];

        int cutoff = 20;
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, Parsers.lp, Parsers.sp, v -> 1.0);
        ItemDistanceModel<Long> dist = new JaccardFeatureItemDistanceModel<>(featureData);
        RelevanceModel<Long, Long> rel = new NoRelevanceModel<>();
        RecommendationMetric<Long, Long> metric = new EILD<>(cutoff, dist, rel);
        SystemMetric<Long, Long> average = new AverageRecommendationMetric<>(metric, numUsers);

        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(Parsers.lp, Parsers.lp);

        format.getReader(recIn).readAll().forEach(average::add);
        System.out.println(average.evaluate());
    }
}
