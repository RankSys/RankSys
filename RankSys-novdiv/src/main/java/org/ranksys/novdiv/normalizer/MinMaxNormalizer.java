package org.ranksys.novdiv.normalizer;

/**
 * Min-max normalizer.
 *
 * @param <I> type of the items
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class MinMaxNormalizer<I> extends StatsBasedNormalizer<I>
{
    @Override
    public double norm(I i, double value)
    {
        return (value - stats.getMin())/(stats.getMax() - stats.getMin());
    }
}
