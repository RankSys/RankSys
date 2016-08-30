/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.examples;

import org.ranksys.core.feature.FeatureData;
import org.ranksys.core.feature.SimpleFeatureData;
import org.ranksys.diversity.distance.metrics.EILD;
import org.ranksys.diversity.intentaware.FeatureIntentModel;
import org.ranksys.diversity.intentaware.IntentModel;
import org.ranksys.diversity.intentaware.metrics.AlphaNDCG;
import org.ranksys.diversity.intentaware.metrics.ERRIA;
import org.ranksys.diversity.sales.metrics.AggregateDiversityMetric;
import org.ranksys.diversity.sales.metrics.GiniIndex;
import org.ranksys.metrics.RecommendationMetric;
import org.ranksys.metrics.SystemMetric;
import org.ranksys.metrics.basic.AverageRecommendationMetric;
import org.ranksys.metrics.basic.NDCG;
import org.ranksys.metrics.basic.Precision;
import org.ranksys.metrics.basic.Recall;
import org.ranksys.metrics.rank.NoDiscountModel;
import org.ranksys.metrics.rank.RankingDiscountModel;
import org.ranksys.metrics.rel.BinaryRelevanceModel;
import org.ranksys.metrics.rel.NoRelevanceModel;
import org.ranksys.novdiv.distance.CosineFeatureItemDistanceModel;
import org.ranksys.novdiv.distance.ItemDistanceModel;
import org.ranksys.novelty.longtail.FDItemNovelty;
import org.ranksys.novelty.longtail.PCItemNovelty;
import org.ranksys.novelty.longtail.metrics.EFD;
import org.ranksys.novelty.longtail.metrics.EPC;
import org.ranksys.novelty.unexp.PDItemNovelty;
import org.ranksys.novelty.unexp.metrics.EPD;

import java.util.HashMap;
import java.util.Map;
import org.ranksys.core.preference.ConcatPreferenceData;
import org.ranksys.core.preference.PreferenceData;
import org.ranksys.core.preference.SimplePreferenceData;

import static org.ranksys.formats.parsing.Parsers.*;
import org.ranksys.formats.feature.SimpleFeaturesReader;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import org.ranksys.formats.rec.RecommendationFormat;
import org.ranksys.formats.rec.SimpleRecommendationFormat;

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
        PreferenceData<Long, Long> trainData = SimplePreferenceData.load(SimpleRatingPreferencesReader.get().read(trainDataPath, lp, lp));
        PreferenceData<Long, Long> testData = SimplePreferenceData.load(SimpleRatingPreferencesReader.get().read(testDataPath, lp, lp));
        PreferenceData<Long, Long> totalData = new ConcatPreferenceData<>(trainData, testData);
        // EVALUATED AT CUTOFF 10
        int cutoff = 10;
        // ITEM - FEATURE file
        FeatureData<Long, String, Double> featureData = SimpleFeatureData.load(SimpleFeaturesReader.get().read(featurePath, lp, sp));
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
