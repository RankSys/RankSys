/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.sales.metrics;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.List;
import java.util.stream.IntStream;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Abstract sales diversity metrics. It handles the counting of how many times
 * an item is recommended.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractSalesDiversityMetric<U, I> extends AbstractSystemMetric<U, I> {

    /**
     * maximum length of the recommendation lists that is evaluated
     */
    protected final int cutoff;
    private final RankingDiscountModel disc;
    private final RelevanceModel<U, I> rel;

    /**
     * Map of item-count.
     */
    protected final Object2DoubleOpenHashMap<I> itemCount;

    /**
     * Map of item-weight.
     */
    protected final Object2DoubleOpenHashMap<I> itemWeight;

    /**
     * Norm for free discovery item novelty models.
     */
    protected double freeNorm;

    /**
     * Number of users.
     */
    protected int numUsers;

    /**
     * Constructor
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     * @param disc ranking discount model
     * @param rel relevance model
     */
    public AbstractSalesDiversityMetric(int cutoff, RankingDiscountModel disc, RelevanceModel<U, I> rel) {
        this.cutoff = cutoff;
        this.disc = disc;
        this.rel = rel;

        this.itemCount = new Object2DoubleOpenHashMap<>();
        this.itemCount.defaultReturnValue(0.0);
        this.itemWeight = new Object2DoubleOpenHashMap<>();
        this.itemWeight.defaultReturnValue(0.0);
        this.freeNorm = 0;
        this.numUsers = 0;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> urm = rel.getModel(recommendation.getUser());
        List<Tuple2od<I>> list = recommendation.getItems();

        int rank = Math.min(cutoff, list.size());
        double userNorm = IntStream.range(0, rank).mapToDouble(disc::disc).sum();

        IntStream.range(0, rank).forEach(k -> {
            I i = list.get(k).v1;
            double d = disc.disc(k);
            double w = d * urm.gain(i) / userNorm;
            itemCount.addTo(i, d);
            itemWeight.addTo(i, w);
        });

        freeNorm += userNorm;
        numUsers++;
    }

    @Override
    public void combine(SystemMetric<U, I> other) {
        AbstractSalesDiversityMetric<U, I> otherM = (AbstractSalesDiversityMetric<U, I>) other;

        otherM.itemCount.object2DoubleEntrySet().forEach(e -> {
            itemCount.addTo(e.getKey(), e.getDoubleValue());
        });
        otherM.itemWeight.object2DoubleEntrySet().forEach(e -> {
            itemWeight.addTo(e.getKey(), e.getDoubleValue());
        });

        freeNorm += otherM.freeNorm;
        numUsers += otherM.numUsers;
    }

    /**
     * Returns the sales novelty of an item.
     *
     * @param i item
     * @return the sales novelty of the item
     */
    protected abstract double nov(I i);

    @Override
    public double evaluate() {
        return itemCount.keySet().stream()
                .mapToDouble((i) -> itemWeight.getDouble(i) * nov(i))
                .sum() / numUsers;
    }

    @Override
    public void reset() {
        this.itemCount.clear();
        this.itemWeight.clear();
        this.freeNorm = 0;
        this.numUsers = 0;
    }

}
