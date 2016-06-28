package es.uam.eps.ir.ranksys.nn.user.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.SetCosineSimilarity;
import es.uam.eps.ir.ranksys.nn.sim.SetJaccardSimilarity;
import es.uam.eps.ir.ranksys.nn.sim.VectorCosineSimilarity;
import es.uam.eps.ir.ranksys.nn.sim.VectorJaccardSimilarity;

public class UserSimilarities {

    public static <U> UserSimilarity<U> setCosine(FastPreferenceData<U, ?> recommenderData, double alpha, boolean dense) {
        return new UserSimilarity<U>(recommenderData, new SetCosineSimilarity(recommenderData, alpha, dense));
    }

    public static <U> UserSimilarity<U> vectorCosine(FastPreferenceData<U, ?> recommenderData, double alpha, boolean dense) {
        return new UserSimilarity<U>(recommenderData, new VectorCosineSimilarity(recommenderData, alpha, dense));
    }

    public static <U> UserSimilarity<U> setJaccard(FastPreferenceData<U, ?> recommenderData, boolean dense) {
        return new UserSimilarity<U>(recommenderData, new SetJaccardSimilarity(recommenderData, dense));
    }

    public static <U> UserSimilarity<U> vectorJaccard(FastPreferenceData<U, ?> recommenderData, boolean dense) {
        return new UserSimilarity<U>(recommenderData, new VectorJaccardSimilarity(recommenderData, dense));
    }

}
