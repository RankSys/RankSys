package org.ranksys.novdiv.normalizer;

/**
 * Normalizer that does not transform the data at all.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <I> type of the items.
 */
public class NoNormalizer<I> implements Normalizer<I>
{
    @Override
    public void add(I i, double val)
    {

    }

    @Override
    public double norm(I i, double value)
    {
        return value;
    }
}
