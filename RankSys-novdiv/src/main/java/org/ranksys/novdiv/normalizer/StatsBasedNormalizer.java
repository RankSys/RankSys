package org.ranksys.novdiv.normalizer;

import org.ranksys.core.util.Stats;

/**
 * Normalizer based on the data statistics.
 *
 * @param <I> type of the items.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public abstract class StatsBasedNormalizer<I> implements Normalizer<I>
{
    /**
     * Data statistics.
     */
    protected final Stats stats = new Stats();

    @Override
    public void add(I i, double val)
    {
        this.stats.accept(val);
    }
}
