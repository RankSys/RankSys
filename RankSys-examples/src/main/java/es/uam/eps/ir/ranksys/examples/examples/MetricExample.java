/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.examples.examples;

import es.uam.eps.ir.ranksys.core.data.ConcatRecommenderData;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.data.SimpleRecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.lp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.sp;
import es.uam.eps.ir.ranksys.diversity.sales.metrics.AggregateDiversityMetric;
import es.uam.eps.ir.ranksys.diversity.sales.metrics.GiniIndex;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.metrics.AlphaNDCG;
import es.uam.eps.ir.ranksys.diversity.intentaware.metrics.ERRIA;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.FDItemNovelty;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.PCItemNovelty;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.PDItemNovelty;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.metrics.EFD;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.metrics.EPC;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.metrics.EPD;
import es.uam.eps.ir.ranksys.diversity.pairwise.CosineFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pairwise.ItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.pairwise.metrics.EILD;
import es.uam.eps.ir.ranksys.metrics.AverageRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.NDCG;
import es.uam.eps.ir.ranksys.metrics.Precision;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import es.uam.eps.ir.ranksys.metrics.rank.NoDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class MetricExample {

    public static void main(String[] args) throws Exception {
        String trainDataPath = args[0];
        String testDataPath = args[1];
        String featurePath = args[2];
        String recIn = args[3];
        Double threshold = Double.parseDouble(args[4]);

        // USER - ITEM - RATING files for train and test
        Parser<Double> parser = (token) -> Double.parseDouble(token.toString().split("\t")[0]); // discard timestamps
        RecommenderData<Long, Long, Double> trainData = SimpleRecommenderData.load(trainDataPath, lp, lp, parser);
        RecommenderData<Long, Long, Double> testData = SimpleRecommenderData.load(testDataPath, lp, lp, parser);
        RecommenderData<Long, Long, Double> totalData = new ConcatRecommenderData<>(trainData, testData);
        // EVALUATED AT CUTOFF 10
        int cutoff = 10;
        // ITEM - FEATURE file
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(featurePath, lp, sp, v -> 1.0);
        // COSINE DISTANCE
        ItemDistanceModel<Long> dist = new CosineFeatureItemDistanceModel<>(featureData);
        // BINARY RELEVANCE
        BinaryRelevanceModel<Long, Long> binRel = new BinaryRelevanceModel<>(false, testData, threshold);
        // NO RELEVANCE
        NoRelevanceModel<Long, Long> norel = new NoRelevanceModel<>();
        // NO RANKING DISCOUNT
        RankingDiscountModel disc = new NoDiscountModel();
        // INTENT MODEL
        IntentModel<Long, Long, String> intentModel = new IntentModel<>(false, testData.getAllUsers(), totalData, featureData);

        Map<String, SystemMetric<Long, Long>> sysMetrics = new HashMap<>();

        ////////////////////////
        // INDIVIDUAL METRICS //
        ////////////////////////
        Map<String, RecommendationMetric<Long, Long>> recMetrics = new HashMap<>();

        // PRECISION
        recMetrics.put("prec", new Precision<>(cutoff, binRel));
        // nDCG
        recMetrics.put("ndcg", new NDCG<>(cutoff, new NDCG.NDCGRelevanceModel<>(false, testData, threshold)));
        // EILD
        recMetrics.put("eild", new EILD<>(cutoff, dist, norel, disc));
        // EPC
        recMetrics.put("epc", new EPC<>(cutoff, new PCItemNovelty<>(trainData), norel, disc));
        // EFD
        recMetrics.put("efd", new EFD<>(cutoff, new FDItemNovelty<>(trainData), norel, disc));
        // EPD
        recMetrics.put("epd", new EPD<>(cutoff, new PDItemNovelty<>(false, trainData, dist), norel, disc));
        // ERR-IA
        recMetrics.put("err-ia", new ERRIA<>(cutoff, intentModel, new ERRIA.ERRRelevanceModel<>(false, testData, threshold)));
        // alpha-nDCG
        recMetrics.put("a-ndcg", new AlphaNDCG<>(cutoff, 0.5, featureData, binRel));

        // AVERAGE VALUES OF RECOMMENDATION METRICS FOR ITEMS IN TEST
        int numUsers = testData.numUsers();
        recMetrics.forEach((name, metric) -> sysMetrics.put(name, new AverageRecommendationMetric<>(metric, numUsers)));

        ////////////////////
        // SYSTEM METRICS //
        ////////////////////
        sysMetrics.put("aggrdiv", new AggregateDiversityMetric<>(cutoff, norel));
        int numItems = totalData.numItems();
        sysMetrics.put("gini", new GiniIndex<>(cutoff, numItems));
        
        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);

        format.getReader(recIn).readAll().forEach(rec -> sysMetrics.values().forEach(metric -> metric.add(rec)));
        
        sysMetrics.forEach((name, metric) -> System.out.println(name + "\t" + metric.evaluate()));
    }
}
