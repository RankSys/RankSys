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
package es.uam.eps.ir.ranksys.tools.examples;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.core.util.parsing.Parsers;
import es.uam.eps.ir.ranksys.diversity.pairwise.ItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pairwise.JaccardFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pairwise.metrics.EILD;
import es.uam.eps.ir.ranksys.metrics.AverageRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
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
