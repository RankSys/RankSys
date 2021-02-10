package org.ranksys.novdiv.normalizer;

import java.util.function.Supplier;

/**
 * Examples of normalizers.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class Normalizers
{
    public static <I> Supplier<Normalizer<I>> noNorm()   { return NoNormalizer::new;}
    public static <I> Supplier<Normalizer<I>> ranksim()  { return RanksimNormalizer::new;}
    public static <I> Supplier<Normalizer<I>> minmax()   { return MinMaxNormalizer::new;}
    public static <I> Supplier<Normalizer<I>> zscore()   { return ZScoreNormalizer::new;}
}
