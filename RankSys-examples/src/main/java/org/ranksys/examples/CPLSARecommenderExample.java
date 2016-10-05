package org.ranksys.examples;

import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.feature.SimpleFastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.*;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.rec.MFRecommender;
import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.rec.runner.RecommenderRunner;
import es.uam.eps.ir.ranksys.rec.runner.fast.FastFilterRecommenderRunner;
import es.uam.eps.ir.ranksys.rec.runner.fast.FastFilters;
import org.ranksys.formats.feature.SimpleFeaturesReader;
import org.ranksys.formats.index.FeatsReader;
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import org.ranksys.formats.rec.RecommendationFormat;
import org.ranksys.formats.rec.SimpleRecommendationFormat;
import org.ranksys.mf.plsa.CPLSAFactorizer;

import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import static org.ranksys.formats.parsing.Parsers.lp;
import static org.ranksys.formats.parsing.Parsers.sp;

public class CPLSARecommenderExample {

    public static void main(String[] args) throws IOException {
        String userPath = args[0];
        String itemPath = args[1];
        String featurePath = args[2];
        String trainDataPath = args[3];
        String testDataPath = args[4];
        String featureDataPath = args[5];

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(userPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemPath, lp));
        FastFeatureIndex<String> featureIndex = SimpleFastFeatureIndex.load(FeatsReader.read(featurePath, sp));
        FastPreferenceData<Long, Long> trainData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(trainDataPath, lp, lp), userIndex, itemIndex);
        FastPreferenceData<Long, Long> testData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(testDataPath, lp, lp), userIndex, itemIndex);
        FastFeatureData<Long, String, Double> featureData = SimpleFastFeatureData.load(SimpleFeaturesReader.get().read(featureDataPath, lp, sp), itemIndex, featureIndex);

        int numIter = 100;

        Factorization<Long, Long> factorization = new CPLSAFactorizer<Long, Long, String>(numIter, featureData).factorize(trainData);
        Recommender<Long, Long> recommender = new MFRecommender<>(userIndex, itemIndex, factorization);

        Set<Long> targetUsers = testData.getUsersWithPreferences().collect(Collectors.toSet());
        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);
        Function<Long, IntPredicate> filter = FastFilters.notInTrain(trainData);
        int maxLength = 100;
        RecommenderRunner<Long, Long> runner = new FastFilterRecommenderRunner<>(userIndex, itemIndex, targetUsers.stream(), filter, maxLength);

        System.out.println("Running cPLSA recommender");
        try (RecommendationFormat.Writer<Long, Long> writer = format.getWriter("cplsa")) {
            runner.run(recommender, writer);
        }
    }
}
