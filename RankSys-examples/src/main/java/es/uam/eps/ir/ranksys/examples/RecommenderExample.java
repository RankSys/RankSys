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

import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import static es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser.ddp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.lp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.vp;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.als.HKVFactorizer;
import es.uam.eps.ir.ranksys.mf.als.PZTFactorizer;
import es.uam.eps.ir.ranksys.mf.plsa.PLSAFactorizer;
import es.uam.eps.ir.ranksys.mf.rec.MFRecommender;
import es.uam.eps.ir.ranksys.nn.item.ItemNeighborhoodRecommender;
import es.uam.eps.ir.ranksys.nn.item.neighborhood.CachedItemNeighborhood;
import es.uam.eps.ir.ranksys.nn.item.neighborhood.ItemNeighborhood;
import es.uam.eps.ir.ranksys.nn.item.neighborhood.TopKItemNeighborhood;
import es.uam.eps.ir.ranksys.nn.item.sim.ItemSimilarity;
import es.uam.eps.ir.ranksys.nn.item.sim.VectorCosineItemSimilarity;
import es.uam.eps.ir.ranksys.nn.user.UserNeighborhoodRecommender;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.TopKUserNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.sim.UserSimilarity;
import es.uam.eps.ir.ranksys.nn.user.sim.VectorCosineUserSimilarity;
import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.rec.fast.basic.PopularityRecommender;
import es.uam.eps.ir.ranksys.rec.fast.basic.RandomRecommender;
import es.uam.eps.ir.ranksys.rec.runner.RecommendationRunner;
import es.uam.eps.ir.ranksys.rec.runner.fast.FastFilterRecommendationRunner;
import es.uam.eps.ir.ranksys.rec.runner.fast.FastFilters;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(userPath, lp);
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(itemPath, lp);
        FastPreferenceData<Long, Long, Void> trainData = SimpleFastPreferenceData.load(trainDataPath, lp, lp, ddp, vp, userIndex, itemIndex);
        FastPreferenceData<Long, Long, Void> testData = SimpleFastPreferenceData.load(testDataPath, lp, lp, ddp, vp, userIndex, itemIndex);

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
            
            UserSimilarity<Long> sim = new VectorCosineUserSimilarity<>(trainData, alpha);
            UserNeighborhood<Long> neighborhood = new TopKUserNeighborhood<>(sim, k);
            
            return new UserNeighborhoodRecommender<>(trainData, neighborhood, q);
        });

        // item-based nearest neighbors
        recMap.put("ib", () -> {
            double alpha = 0.5;
            int k = 10;
            int q = 1;
            
            ItemSimilarity<Long> sim = new VectorCosineItemSimilarity<>(trainData, alpha);
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
            
            return new MFRecommender<>(trainData, factorization);
        });

        // implicit matrix factorization of Pilaszy et al. 2010
        recMap.put("pzt", () -> {
            int k = 50;
            double lambda = 0.1;
            double alpha = 1.0;
            DoubleUnaryOperator confidence = x -> 1 + alpha * x;
            int numIter = 20;
            
            Factorization<Long, Long> factorization = new PZTFactorizer<Long, Long>(lambda, confidence, numIter).factorize(k, trainData);
            
            return new MFRecommender<>(trainData, factorization);
        });

        // probabilistic latent semantic analysis of Hofmann 2004
        recMap.put("plsa", () -> {
            int k = 50;
            int numIter = 100;
            
            Factorization<Long, Long> factorization = new PLSAFactorizer<Long, Long>(numIter).factorize(k, trainData);
            
            return new MFRecommender<>(trainData, factorization);
        });

        ////////////////////////////////
        // GENERATING RECOMMENDATIONS //
        ////////////////////////////////
        Set<Long> targetUsers = testData.getUsersWithPreferences().collect(Collectors.toSet());
        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);
        Function<Long, IntPredicate> filter = FastFilters.notInTrain(trainData);
        int maxLength = 100;
        RecommendationRunner<Long, Long> runner = new FastFilterRecommendationRunner<>(userIndex, itemIndex, targetUsers, format, filter, maxLength);
        
        recMap.forEach((name, recommender) -> {
            try {
                System.out.println("Running " + name);
                runner.run(recommender.get(), name);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }
}
