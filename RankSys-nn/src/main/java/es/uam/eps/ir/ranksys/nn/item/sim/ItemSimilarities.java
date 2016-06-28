package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.SetCosineSimilarity;
import es.uam.eps.ir.ranksys.nn.sim.SetJaccardSimilarity;
import es.uam.eps.ir.ranksys.nn.sim.VectorCosineSimilarity;
import es.uam.eps.ir.ranksys.nn.sim.VectorJaccardSimilarity;

public class ItemSimilarities {

    public static <I> ItemSimilarity<I> setCosine(FastPreferenceData<?, I> preferences, double alpha, boolean dense) {
        return new ItemSimilarity<I>(preferences, new SetCosineSimilarity(new TransposedPreferenceData<>(preferences), alpha, dense));
    }

    public static <I> ItemSimilarity<I> vectorCosine(FastPreferenceData<?, I> preferences, double alpha, boolean dense) {
        return new ItemSimilarity<I>(preferences, new VectorCosineSimilarity(new TransposedPreferenceData<>(preferences), alpha, dense));
    }

    public static <I> ItemSimilarity<I> setJaccard(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<I>(preferences, new SetJaccardSimilarity(new TransposedPreferenceData<>(preferences), dense));
    }

    public static <I> ItemSimilarity<I> vectorJaccard(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<I>(preferences, new VectorJaccardSimilarity(new TransposedPreferenceData<>(preferences), dense));
    }

}
