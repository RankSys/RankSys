package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.Similarities;

public class ItemSimilarities {

    public static <I> ItemSimilarity<I> setCosine(FastPreferenceData<?, I> preferences, double alpha, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.setCosine(new TransposedPreferenceData<>(preferences), dense, alpha));
    }

    public static <I> ItemSimilarity<I> vectorCosine(FastPreferenceData<?, I> preferences, double alpha, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.vectorCosine(new TransposedPreferenceData<>(preferences), dense));
    }

    public static <I> ItemSimilarity<I> setJaccard(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.setJaccard(new TransposedPreferenceData<>(preferences), dense));
    }

    public static <I> ItemSimilarity<I> vectorJaccard(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.vectorJaccard(new TransposedPreferenceData<>(preferences), dense));
    }
    
    public static <I> ItemSimilarity<I> logLikelihood(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.logLikelihood(new TransposedPreferenceData<>(preferences), dense));
    }

}
