/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.examples;

import cc.mallet.topics.ParallelTopicModel;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jooq.lambda.Unchecked;
import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.item.fast.SimpleFastItemFeatureData;
import org.ranksys.core.index.fast.*;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.preference.fast.SimpleFastPreferenceData;
import org.ranksys.evaluation.runner.RecommenderRunner;
import org.ranksys.evaluation.runner.fast.FastFilterRecommenderRunner;
import org.ranksys.evaluation.runner.fast.FastFilters;
import org.ranksys.formats.feature.SimpleFeaturesReader;
import org.ranksys.formats.index.FeatsReader;
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import static org.ranksys.formats.parsing.Parsers.lp;
import static org.ranksys.formats.parsing.Parsers.sp;

import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import org.ranksys.formats.rec.RecommendationFormat;
import org.ranksys.formats.rec.SimpleRecommendationFormat;
import org.ranksys.recommenders.Recommender;
import org.ranksys.recommenders.basic.PopularityRecommender;
import org.ranksys.recommenders.basic.RandomRecommender;
import org.ranksys.recommenders.content.RocchioRecommender;
import org.ranksys.recommenders.content.SimpleUserProfile;
import org.ranksys.recommenders.content.item.sim.ItemFeatureSimilarities;
import org.ranksys.recommenders.content.item.sim.ItemFeatureSimilarity;
import org.ranksys.recommenders.content.useritem.sim.UserItemFeatureSimilarities;
import org.ranksys.recommenders.content.useritem.sim.UserItemFeatureSimilarity;
import org.ranksys.recommenders.fm.PreferenceFM;
import org.ranksys.recommenders.fm.learner.BPRLearner;
import org.ranksys.recommenders.fm.learner.RMSELearner;
import org.ranksys.recommenders.fm.rec.FMRecommender;
import org.ranksys.recommenders.lda.LDAModelEstimator;
import org.ranksys.recommenders.lda.LDARecommender;
import org.ranksys.recommenders.mf.Factorization;
import org.ranksys.recommenders.mf.als.HKVFactorizer;
import org.ranksys.recommenders.mf.als.ImplicitPZTFactorizer;
import org.ranksys.recommenders.mf.als.MixedPZTFactorizer;
import org.ranksys.recommenders.mf.als.PZTFactorizer;
import org.ranksys.recommenders.mf.plsa.PLSAFactorizer;
import org.ranksys.recommenders.mf.rec.MFRecommender;
import org.ranksys.recommenders.nn.item.ItemNeighborhoodRecommender;
import org.ranksys.recommenders.nn.item.neighborhood.ItemNeighborhood;
import org.ranksys.recommenders.nn.item.neighborhood.ItemNeighborhoods;
import org.ranksys.recommenders.nn.item.sim.ItemSimilarities;
import org.ranksys.recommenders.nn.item.sim.ItemSimilarity;
import org.ranksys.recommenders.nn.user.UserNeighborhoodRecommender;
import org.ranksys.recommenders.nn.user.neighborhood.UserNeighborhood;
import org.ranksys.recommenders.nn.user.neighborhood.UserNeighborhoods;
import org.ranksys.recommenders.nn.user.sim.UserSimilarities;
import org.ranksys.recommenders.nn.user.sim.UserSimilarity;

/**
 * Example main of recommendations.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class RecommenderExample {

    public static void main(String[] args) throws IOException {
        String userPath = args[0];
        String itemPath = args[1];
        String trainDataPath = args[2];
        String testDataPath = args[3];

        String featuresPath = args[4];
        String featDataPath = args[5];

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(userPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemPath, lp));
        FastPreferenceData<Long, Long> trainData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(trainDataPath, lp, lp), userIndex, itemIndex);
        FastPreferenceData<Long, Long> testData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(testDataPath, lp, lp), userIndex, itemIndex);

        FastFeatureIndex<String> featIndex = SimpleFastFeatureIndex.load(FeatsReader.read(featuresPath, sp));
        FastItemFeatureData<Long, String, Double> itemFeatData = SimpleFastItemFeatureData.load(SimpleFeaturesReader.get().read(featDataPath,lp, sp), itemIndex, featIndex);

        //////////////////
        // RECOMMENDERS //
        //////////////////
        Map<String, Supplier<Recommender<Long, Long>>> recMap = new HashMap<>();

        // random recommendation
        recMap.put("rnd", () -> new RandomRecommender<>(trainData, trainData));

        // most-popular recommendation
        recMap.put("pop", () -> new PopularityRecommender<>(trainData));

        // user-based nearest neighbors
        recMap.put("ub", () -> {
            int k = 100;
            int q = 1;

            UserSimilarity<Long> sim = UserSimilarities.vectorCosine(trainData, true);
            UserNeighborhood<Long> neighborhood = UserNeighborhoods.topK(sim, k);

            return new UserNeighborhoodRecommender<>(trainData, neighborhood, q);
        });

        // item-based nearest neighbors
        recMap.put("ib", () -> {
            int k = 10;
            int q = 1;

            ItemSimilarity<Long> sim = ItemSimilarities.vectorCosine(trainData, true);
            ItemNeighborhood<Long> neighborhood = ItemNeighborhoods.cached(ItemNeighborhoods.topK(sim, k));

            return new ItemNeighborhoodRecommender<>(trainData, neighborhood, q);
        });

        // implicit matrix factorization of Hu et al. 2008
        recMap.put("hkv", () -> {
            int k = 50;
            double lambda = 0.1;
            double alpha = 1.0;
            DoubleUnaryOperator confidence = x -> 1 + alpha * x;
            int numIter = 20;

            Factorization<Long, Long> factorization = new HKVFactorizer<Long, Long>(lambda, confidence, numIter).factorize(k, trainData);

            return new MFRecommender<>(userIndex, itemIndex, factorization);
        });

        // implicit matrix factorization of Pilaszy et al. 2010
        recMap.put("pzt", () -> {
            int k = 50;
            double lambda = 0.1;
            double alpha = 1.0;
            DoubleUnaryOperator confidence = x -> 1 + alpha * x;
            int numIter = 20;

            Factorization<Long, Long> factorization = new PZTFactorizer<Long, Long>(lambda, confidence, numIter).factorize(k, trainData);

            return new MFRecommender<>(userIndex, itemIndex, factorization);
        });

        // aux implicit matrix factorization of Pilaszy et al. 2010
        recMap.put("ipzt", () -> {
            int k = 50;
            double lambda = 0.1;
            double alpha = 1.0;
            DoubleUnaryOperator confidence = x -> 1 + alpha * x;
            int numIter = 20;

            Factorization<Long, Long> factorization = new ImplicitPZTFactorizer<Long, Long>(lambda, confidence, numIter).factorize(k, trainData);

            return new MFRecommender<>(userIndex, itemIndex, factorization);
        });

        // aux implicit matrix factorization of Pilaszy et al. 2010
        recMap.put("mpzt1", () -> {
            int k = 50;
            double lambda = 0.1;
            double alpha = 1.0;
            DoubleUnaryOperator confidence = x -> 1 + alpha * x;
            int numIter = 20;

            Factorization<Long, Long> factorization = new MixedPZTFactorizer<Long, Long>(lambda, confidence, numIter, true).factorize(k, trainData);

            return new MFRecommender<>(userIndex, itemIndex, factorization);
        });

        // aux implicit matrix factorization of Pilaszy et al. 2010
        recMap.put("mpzt0", () -> {
            int k = 50;
            double lambda = 0.1;
            double alpha = 1.0;
            DoubleUnaryOperator confidence = x -> 1 + alpha * x;
            int numIter = 20;

            Factorization<Long, Long> factorization = new MixedPZTFactorizer<Long, Long>(lambda, confidence, numIter, false).factorize(k, trainData);

            return new MFRecommender<>(userIndex, itemIndex, factorization);
        });

        // probabilistic latent semantic analysis of Hofmann 2004
        recMap.put("plsa", () -> {
            int k = 50;
            int numIter = 100;

            Factorization<Long, Long> factorization = new PLSAFactorizer<Long, Long>(numIter).factorize(k, trainData);

            return new MFRecommender<>(userIndex, itemIndex, factorization);
        });

        // LDA topic modelling by Blei et al. 2003
        recMap.put("lda", Unchecked.supplier(() -> {
            int k = 50;
            double alpha = 1.0;
            double beta = 0.01;
            int numIter = 200;
            int burninPeriod = 50;

            ParallelTopicModel topicModel = LDAModelEstimator.estimate(trainData, k, alpha, beta, numIter, burninPeriod);

            return new LDARecommender<>(userIndex, itemIndex, topicModel);
        }));

        // Factorisation machine using a BRP-like loss
        recMap.put("fm-bpr", Unchecked.supplier(() -> {

            double learnRate = 0.01;
            int numIter = 200;
            double regW = 0.01;
            double regM = 0.01;
            int K = 100;
            double sdev = 0.1;

            PreferenceFM<Long, Long> prefFm = new BPRLearner<>(learnRate, numIter, regW, regM, userIndex, itemIndex).learn(trainData, testData, K, sdev);

            return new FMRecommender<>(prefFm);
        }));

        // Factorisation machine using a RMSE-like loss with balanced sampling of negative
        // instances
        recMap.put("fm-rmse", Unchecked.supplier(() -> {

            double learnRate = 0.01;
            int numIter = 50;
            double regB = 0.01;
            double regW = 0.01;
            double regM = 0.01;
            double negativeProp = 2.0;
            int K = 100;
            double sdev = 0.1;
            
            PreferenceFM<Long, Long> prefFm = new RMSELearner<>(learnRate, numIter, regB, regW, regM, negativeProp, userIndex, itemIndex).learn(trainData, testData, K, sdev);

            return new FMRecommender<>(prefFm);
        }));

        // Content-based algorithm.
        recMap.put("rocchio", () -> {
            SimpleUserProfile<Long, String> userProfile = SimpleUserProfile.load(trainData, itemFeatData);
            UserItemFeatureSimilarity<Long, Long> similarity = UserItemFeatureSimilarities.vectorCosine(userProfile, itemFeatData, false);
            return new RocchioRecommender<>(similarity);
        });

        // Content-based kNN
        recMap.put("content-ib", () -> {
            double alpha = 0.5;
            int k = 10;
            int q = 1;
            ItemFeatureSimilarity<Long> similarity = ItemFeatureSimilarities.setCosine(itemFeatData, false, alpha);
            ItemNeighborhood<Long> neigh = ItemNeighborhoods.topK(similarity, k);

            return new ItemNeighborhoodRecommender<>(trainData, neigh, q);
        });

        ////////////////////////////////
        // GENERATING RECOMMENDATIONS //
        ////////////////////////////////
        Set<Long> targetUsers = testData.getUsersWithPreferences().collect(Collectors.toSet());
        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);
        Function<Long, IntPredicate> filter = FastFilters.notInTrain(trainData);
        int maxLength = 100;
        RecommenderRunner<Long, Long> runner = new FastFilterRecommenderRunner<>(userIndex, itemIndex, targetUsers.stream(), filter, maxLength);

        recMap.forEach(Unchecked.biConsumer((name, recommender) -> {
            System.out.println("Running " + name);
            try (RecommendationFormat.Writer<Long, Long> writer = format.getWriter(name)) {
                runner.run(recommender.get(), writer);
            }
        }));
    }
}
