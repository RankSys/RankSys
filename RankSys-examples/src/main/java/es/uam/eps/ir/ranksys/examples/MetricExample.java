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
import es.uam.eps.ir.ranksys.core.preference.ConcatPreferenceData;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData;
import es.uam.eps.ir.ranksys.diversity.distance.metrics.EILD;
import es.uam.eps.ir.ranksys.diversity.intentaware.FeatureIntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.metrics.AlphaNDCG;
import es.uam.eps.ir.ranksys.diversity.intentaware.metrics.ERRIA;
import es.uam.eps.ir.ranksys.diversity.sales.metrics.AggregateDiversityMetric;
import es.uam.eps.ir.ranksys.diversity.sales.metrics.GiniIndex;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import es.uam.eps.ir.ranksys.metrics.basic.AverageRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.basic.NDCG;
import es.uam.eps.ir.ranksys.metrics.basic.Precision;
import es.uam.eps.ir.ranksys.metrics.basic.Recall;
import es.uam.eps.ir.ranksys.metrics.rank.NoDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import es.uam.eps.ir.ranksys.novdiv.distance.CosineFeatureItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.novelty.longtail.FDItemNovelty;
import es.uam.eps.ir.ranksys.novelty.longtail.PCItemNovelty;
import es.uam.eps.ir.ranksys.novelty.longtail.metrics.EFD;
import es.uam.eps.ir.ranksys.novelty.longtail.metrics.EPC;
import es.uam.eps.ir.ranksys.novelty.unexp.PDItemNovelty;
import es.uam.eps.ir.ranksys.novelty.unexp.metrics.EPD;

import java.util.HashMap;
import java.util.Map;

import static es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser.ddp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.*;

/**
 * Example main of metrics.
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
        PreferenceData<Long, Long, Void> trainData = SimplePreferenceData.load(trainDataPath, lp, lp, ddp, vp);
        PreferenceData<Long, Long, Void> testData = SimplePreferenceData.load(testDataPath, lp, lp, ddp, vp);
        PreferenceData<Long, Long, Void> totalData = new ConcatPreferenceData<>(trainData, testData);
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
        IntentModel<Long, Long, String> intentModel = new FeatureIntentModel<>(totalData, featureData);

        Map<String, SystemMetric<Long, Long>> sysMetrics = new HashMap<>();

        ////////////////////////
        // INDIVIDUAL METRICS //
        ////////////////////////
        Map<String, RecommendationMetric<Long, Long>> recMetrics = new HashMap<>();

        // PRECISION
        recMetrics.put("prec", new Precision<>(cutoff, binRel));
        // RECALL
        recMetrics.put("recall", new Recall<>(cutoff, binRel));
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
        int numUsers = testData.numUsersWithPreferences();
        recMetrics.forEach((name, metric) -> sysMetrics.put(name, new AverageRecommendationMetric<>(metric, numUsers)));

        ////////////////////
        // SYSTEM METRICS //
        ////////////////////
        sysMetrics.put("aggrdiv", new AggregateDiversityMetric<>(cutoff, norel));
        int numItems = totalData.numItemsWithPreferences();
        sysMetrics.put("gini", new GiniIndex<>(cutoff, numItems));

        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);

        format.getReader(recIn).readAll().forEach(rec -> sysMetrics.values().forEach(metric -> metric.add(rec)));

        sysMetrics.forEach((name, metric) -> System.out.println(name + "\t" + metric.evaluate()));
    }
}
