package org.ranksys.examples;

import es.uam.eps.ir.ranksys.diversity.intentaware.reranking.XQuAD;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.feature.SimpleFastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.novdiv.reranking.Reranker;
import org.jooq.lambda.Unchecked;
import org.ranksys.diversity.intentaware.CPLSAIAFactorizationModelFactory;
import org.ranksys.formats.feature.SimpleFeaturesReader;
import org.ranksys.formats.index.FeatsReader;
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import org.ranksys.formats.rec.RecommendationFormat;
import org.ranksys.formats.rec.SimpleRecommendationFormat;

import static org.ranksys.formats.parsing.Parsers.lp;
import static org.ranksys.formats.parsing.Parsers.sp;

public class CPLSARerankerExample {

    public static void main(String[] args) throws Exception {
        String userPath = args[0];
        String itemPath = args[1];
        String featurePath = args[2];
        String trainDataPath = args[3];
        String featureDataPath = args[4];
        String recIn = args[5];

        double lambda = 0.5;
        int cutoff = 100;
        int numIter = 100;

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(userPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemPath, lp));
        FastFeatureIndex<String> featureIndex = SimpleFastFeatureIndex.load(FeatsReader.read(featurePath, sp));
        FastPreferenceData<Long, Long> trainData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(trainDataPath, lp, lp), userIndex, itemIndex);
        FastFeatureData<Long, String, Double> featureData = SimpleFastFeatureData.load(SimpleFeaturesReader.get().read(featureDataPath, lp, sp), itemIndex, featureIndex);

        CPLSAIAFactorizationModelFactory<Long, Long, String> cPLSAModel = new CPLSAIAFactorizationModelFactory<>(numIter, trainData,featureData);
        Reranker<Long, Long> reranker = new XQuAD<>(cPLSAModel.getAspectModel(), lambda, cutoff, true);

        RecommendationFormat<Long, Long> format = new SimpleRecommendationFormat<>(lp, lp);

        System.out.println("Running xQuAD with cPLSA aspect model");
        String recOut = String.format("%s_xQuAD_cplsa_%.1f", recIn, lambda);
        try (RecommendationFormat.Writer<Long, Long> writer = format.getWriter(recOut)) {
            format.getReader(recIn).readAll()
                    .map(rec -> reranker.rerankRecommendation(rec, cutoff))
                    .forEach(Unchecked.consumer(writer::write));
        }
    }
}
