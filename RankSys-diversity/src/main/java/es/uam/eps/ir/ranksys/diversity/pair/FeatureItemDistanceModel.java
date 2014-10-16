/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.pair;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public abstract  class FeatureItemDistanceModel<I, F, V> implements ItemDistanceModel<I> {

    private final FeatureData<I, F, V> featureData;

    public FeatureItemDistanceModel(FeatureData<I, F, V> featureData) {
        this.featureData = featureData;
    }

    @Override
    public double dist(I i, I j) {
        return dist(featureData.getItemFeatures(i), featureData.getItemFeatures(j));
    }

    public abstract double dist(Stream<IdValuePair<F, V>> features1, Stream<IdValuePair<F, V>> features2);
}
