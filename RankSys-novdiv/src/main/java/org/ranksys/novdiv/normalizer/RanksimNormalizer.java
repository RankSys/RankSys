package org.ranksys.novdiv.normalizer;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Ranksim normalizer.
 *
 * @param <I> type of the items.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class RanksimNormalizer<I> implements Normalizer<I>
{
    /**
     * True if the map linking items with its ranking has been generated.
     */
    private boolean isSorted;
    /**
     * The actual ranking.
     */
    private final TreeSet<Tuple2od<I>> ranking;
    /**
     * A mapping between item id and ranking position.
     */
    private final Object2IntMap<I> map = new Object2IntOpenHashMap<>();

    /**
     * Comparator for sorting the tuples in the tree set.
     */
    private Comparator<Tuple2od<I>> comp = (Tuple2od<I> t, Tuple2od<I> t1) ->
    {
        double val = Double.compare(t1.v2, t.v2);

        if(val == 0.0)
        {
            val = ((Comparable<I>)t1.v1).compareTo(t.v1);
        }

        return Double.compare(val, 0.0);
    };

    /**
     * Constructor.
     */
    public RanksimNormalizer()
    {
        super();
        this.isSorted = false;
        this.ranking = new TreeSet<>(comp);
    }

    @Override
    public void add(I i, double val)
    {
        this.isSorted = false;
        this.ranking.add(new Tuple2od<>(i, val));
    }

    @Override
    public double norm(I i, double value)
    {
        if(!this.isSorted)
        {
            TreeSet<Tuple2od<I>> cloned = new TreeSet<>(ranking);
            map.clear();
            int j = 0;
            while(!cloned.isEmpty())
            {
                Tuple2od<I> t = cloned.pollFirst();
                map.put(t.v1, j);
                ++j;
            }
            this.isSorted = true;
        }

        double pos = map.getInt(i);
        double size = map.size();
        return 1.0 - pos/size;
    }
}
