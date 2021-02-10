package org.ranksys.novdiv.normalizer;

import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Interface for normalization.
 * @param <I> type of the items.
 */
public interface Normalizer<I>
{
    /**
     * Adds an item-value tuple to the list.
     * @param tuple the item-value tuple.
     */
    default void add(Tuple2od<I> tuple)
    {
        this.add(tuple.v1, tuple.v2);
    }

    /**
     * Adds an item-value pair to the list.
     * @param i the item identifier.
     * @param val the value of the item.
     */
    void add(I i, double val);

    /**
     * Given an item-value pair, it computes the normalized value.
     * @param tuple an item-value pair.
     * @return the normalized value for the tuple
     */
    default double norm(Tuple2od<I> tuple)
    {
        return this.norm(tuple.v1, tuple.v2);
    }

    /**
     * Given an item-value pair, it computes the normalized value.
     * @param i the item identifier.
     * @param value the value.
     * @return the normalized value.
     */
    double norm(I i, double value);

}
