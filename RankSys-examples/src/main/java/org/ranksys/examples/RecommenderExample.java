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
import static org.ranksys.formats.parsing.Parsers.lp;
import org.ranksys.fast.index.FastItemIndex;
import org.ranksys.fast.index.FastUserIndex;
import org.ranksys.fast.index.SimpleFastItemIndex;
import org.ranksys.fast.index.SimpleFastUserIndex;
import org.ranksys.fast.preference.FastPreferenceData;
import org.ranksys.fast.preference.SimpleFastPreferenceData;
import org.ranksys.mf.Factorization;
import org.ranksys.mf.als.HKVFactorizer;
import org.ranksys.mf.als.PZTFactorizer;
import org.ranksys.mf.plsa.PLSAFactorizer;
import org.ranksys.mf.rec.MFRecommender;
import org.ranksys.nn.item.ItemNeighborhoodRecommender;
import org.ranksys.nn.item.neighborhood.CachedItemNeighborhood;
import org.ranksys.nn.item.neighborhood.ItemNeighborhood;
import org.ranksys.nn.item.neighborhood.TopKItemNeighborhood;
import org.ranksys.nn.item.sim.ItemSimilarity;
import org.ranksys.nn.item.sim.VectorCosineItemSimilarity;
import org.ranksys.nn.user.UserNeighborhoodRecommender;
import org.ranksys.nn.user.neighborhood.TopKUserNeighborhood;
import org.ranksys.nn.user.neighborhood.UserNeighborhood;
import org.ranksys.nn.user.sim.UserSimilarity;
import org.ranksys.nn.user.sim.VectorCosineUserSimilarity;
import org.ranksys.rec.Recommender;
import org.ranksys.rec.fast.basic.PopularityRecommender;
import org.ranksys.rec.fast.basic.RandomRecommender;
import org.ranksys.rec.runner.RecommenderRunner;
import org.ranksys.rec.runner.fast.FastFilterRecommenderRunner;
import org.ranksys.rec.runner.fast.FastFilters;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jooq.lambda.Unchecked;
import org.ranksys.fm.PreferenceFM;
import org.ranksys.fm.data.BPRPreferenceFMData;
import org.ranksys.fm.data.OneClassPreferenceFMData;
import org.ranksys.fm.rec.FMRecommender;
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import org.ranksys.formats.rec.RecommendationFormat;
import org.ranksys.formats.rec.SimpleRecommendationFormat;
import org.ranksys.javafm.FM;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.learner.gd.BPR;
import static org.ranksys.javafm.learner.gd.PointWiseError.rmse;
import org.ranksys.javafm.learner.gd.PointWiseGradientDescent;
import org.ranksys.lda.LDAModelEstimator;
import org.ranksys.lda.LDARecommender;

/**
 * Example main of recommendations.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class RecommenderExample {

    public static void main(String[] args) throws IOException {
        String userPath = args[0];
        String itemPath = args[1];
        String trainDataPath = args[2];
        String testDataPath = args[3];

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(userPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemPath, lp));
        FastPreferenceData<Long, Long> trainData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(trainDataPath, lp, lp), userIndex, itemIndex);
        FastPreferenceData<Long, Long> testData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(testDataPath, lp, lp), userIndex, itemIndex);

        //////////////////
        // RECOMMENDERS //
        //////////////////
        Map<String, Supplier<Recommender<Long, Long>>> recMap = new HashMap<>();

        // random recommendation
        recMap.put("rnd", () -> {
            return new RandomRecommender<>(trainData, trainData);
        });

        // most-popular recommendation
        recMap.put("pop", () -> {
            return new PopularityRecommender<>(trainData);
        });

        // user-based nearest neighbors
        recMap.put("ub", () -> {
            double alpha = 0.5;
            int k = 100;
            int q = 1;

            UserSimilarity<Long> sim = new VectorCosineUserSimilarity<>(trainData, alpha, true);
            UserNeighborhood<Long> neighborhood = new TopKUserNeighborhood<>(sim, k);

            return new UserNeighborhoodRecommender<>(trainData, neighborhood, q);
        });

        // item-based nearest neighbors
        recMap.put("ib", () -> {
            double alpha = 0.5;
            int k = 10;
            int q = 1;

            ItemSimilarity<Long> sim = new VectorCosineItemSimilarity<>(trainData, alpha, true);
            ItemNeighborhood<Long> neighborhood = new TopKItemNeighborhood<>(sim, k);
            neighborhood = new CachedItemNeighborhood<>(neighborhood);

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
            BPRPreferenceFMData fmTrain = new BPRPreferenceFMData(trainData);
            BPRPreferenceFMData fmTest = new BPRPreferenceFMData(testData);

            double learnRate = 0.01;
            int numIter = 200;
            double sdev = 0.1;
            double[] regW = new double[fmTrain.numFeatures()];
            Arrays.fill(regW, 0.01);
            double[] regM = new double[fmTrain.numFeatures()];
            Arrays.fill(regM, 0.01);
            int K = 100;

            FM fm = new FM(fmTrain.numFeatures(), K, new Random(), sdev);

            new BPR(learnRate, numIter, regW, regM).learn(fm, fmTrain, fmTest);

            PreferenceFM<Long, Long> prefFm = new PreferenceFM<>(userIndex, itemIndex, fm);

            return new FMRecommender<Long, Long>(prefFm);
        }));

        // Factorisation machine usinga RMSE-like loss with balanced sampling of negative
        // instances
        recMap.put("fm-rmse", Unchecked.supplier(() -> {
            double negativeProp = 2.0;
            FMData fmTrain = new OneClassPreferenceFMData(trainData, negativeProp);
            FMData fmTest = new OneClassPreferenceFMData(testData, negativeProp);

            double learnRate = 0.01;
            int numIter = 50;
            double sdev = 0.1;
            double regB = 0.01;
            double[] regW = new double[fmTrain.numFeatures()];
            Arrays.fill(regW, 0.01);
            double[] regM = new double[fmTrain.numFeatures()];
            Arrays.fill(regM, 0.01);
            int K = 100;

            FM fm = new FM(fmTrain.numFeatures(), K, new Random(), sdev);

            new PointWiseGradientDescent(learnRate, numIter, rmse(), regB, regW, regM).learn(fm, fmTrain, fmTest);

            PreferenceFM<Long, Long> prefFm = new PreferenceFM<>(userIndex, itemIndex, fm);

            return new FMRecommender<Long, Long>(prefFm);
        }));

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
