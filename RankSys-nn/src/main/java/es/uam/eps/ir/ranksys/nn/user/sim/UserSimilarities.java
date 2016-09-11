package es.uam.eps.ir.ranksys.nn.user.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.Similarities;

public class UserSimilarities {

    public static <U> UserSimilarity<U> setCosine(FastPreferenceData<U, ?> recommenderData, double alpha, boolean dense) {
        return new UserSimilarity<>(recommenderData, Similarities.setCosine(recommenderData, dense, alpha));
    }

    public static <U> UserSimilarity<U> vectorCosine(FastPreferenceData<U, ?> recommenderData, boolean dense) {
        return new UserSimilarity<>(recommenderData, Similarities.vectorCosine(recommenderData, dense));
    }

    public static <U> UserSimilarity<U> setJaccard(FastPreferenceData<U, ?> recommenderData, boolean dense) {
        return new UserSimilarity<>(recommenderData, Similarities.setJaccard(recommenderData, dense));
    }

    public static <U> UserSimilarity<U> vectorJaccard(FastPreferenceData<U, ?> recommenderData, boolean dense) {
        return new UserSimilarity<>(recommenderData, Similarities.vectorJaccard(recommenderData, dense));
    }
    
    public static <U> UserSimilarity<U> logLikelihood(FastPreferenceData<U, ?> preferences, boolean dense) {
        return new UserSimilarity<>(preferences, Similarities.logLikelihood(preferences, dense));
    }

}
