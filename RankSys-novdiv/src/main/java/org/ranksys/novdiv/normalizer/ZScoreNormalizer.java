package org.ranksys.novdiv.normalizer;

import org.ranksys.core.index.fast.FastItemIndex;

/**
 * Z-Score normalizer. Normalizes the values so they follow a
 * normal distribution with 0 mean and variance 1.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <I> type of the items.
 */
public class ZScoreNormalizer<I> extends StatsBasedNormalizer<I>
{
    @Override
    public double norm(I i, double value)
    {
        return (value - stats.getMean())/stats.getStandardDeviation();
    }
}
