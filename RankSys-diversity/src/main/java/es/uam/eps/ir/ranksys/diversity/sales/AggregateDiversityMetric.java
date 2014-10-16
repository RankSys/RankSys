/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.sales;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric.AbstractFactory;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import static java.lang.Double.isNaN;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author saul
 */
public class AggregateDiversityMetric<U, I> extends AbstractSystemMetric<U, I, Double> {

    private final double relThreshold;
    private final Set<I> recommendedItems;
    private final int cutoff;

    public AggregateDiversityMetric(RecommenderData<U, I, Double> testData, int cutoff) {
        this(testData, cutoff, Double.NaN);
    }

    public AggregateDiversityMetric(RecommenderData<U, I, Double> testData, int cutoff, double relThreshold) {
        super(testData);
        this.relThreshold = relThreshold;
        this.recommendedItems = new HashSet<>();
        this.cutoff = cutoff;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        U u = recommendation.getUser();
        Set<I> relSet = new HashSet<>();
        if (!isNaN(relThreshold)) {
            relSet = relSet(u);
        }
        int rank = 0;
        for (IdDoublePair<I> ivp : recommendation.getItems()) {
            if (isNaN(relThreshold) || relSet.contains(ivp.id)) {
                recommendedItems.add(ivp.id);
            }
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
    }

    @Override
    public double evaluate() {
        return recommendedItems.size();
    }

    private Set<I> relSet(U user) {
        Set<I> relSet = new HashSet<>();

        for (IdValuePair<I, Double> pair : testData.getUserPreferences(user)) {
            if (pair.v >= relThreshold) {
                relSet.add(pair.id);
            }
        }

        return relSet;
    }

    public static class Factory<U, I> extends AbstractFactory<U, I, Double> {

        private final int cutoff;
        private final double relThreshold;

        public Factory(RecommenderData<U, I, Double> testData, int cutoff) {
            this(testData, cutoff, Double.NaN);
        }

        public Factory(RecommenderData<U, I, Double> testData, int cutoff, double relThreshold) {
            super(testData);
            this.cutoff = cutoff;
            this.relThreshold = relThreshold;
        }

        @Override
        public SystemMetric<U, I> getInstance() {
            return new AggregateDiversityMetric<>(testData, cutoff, relThreshold);
        }

    }
}
