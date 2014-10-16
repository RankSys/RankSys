/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.pair;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public class JaccardFeatureItemDistanceModel<I, F> extends FeatureItemDistanceModel<I, F, Double> {

    public JaccardFeatureItemDistanceModel(FeatureData<I, F, Double> featureData) {
        super(featureData);
    }

    @Override
    public double dist(Stream<IdValuePair<F, Double>> features1, Stream<IdValuePair<F, Double>> features2) {
        TObjectDoubleMap<F> auxMap = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);
        double[] comps = {0.0, 0.0, 0.0};

        features1.forEach(fv -> {
            auxMap.put(fv.id, fv.v);
            comps[0] += fv.v * fv.v;
        });
        
        if (comps[0] == 0) {
            return Double.NaN;
        }

        features2.forEach(fv -> {
            comps[1] += fv.v * fv.v;
            comps[2] += fv.v * auxMap.get(fv.id);
        });
        
        if (comps[1] == 0) {
            return Double.NaN;
        }
        
        return 1 - comps[2] / (comps[0] + comps[1] - comps[2]);
    }

}
