package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.nn.item.sim.ItemSimilarity;
import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.ThresholdNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.TopKNeighborhood;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.stream.Stream;

import static org.ranksys.core.util.tuples.Tuples.tuple;

public class ItemNeighborhoods {

    public static <I> ItemNeighborhood<I> topK(ItemSimilarity<I> similarity, int k) {
        return new ItemNeighborhood<I>(similarity, new TopKNeighborhood(similarity.similarity(), k));
    }

    public static <I> ItemNeighborhood<I> threshold(ItemSimilarity<I> similarity, double threshold) {
        return new ItemNeighborhood<I>(similarity, new ThresholdNeighborhood(similarity.similarity(), threshold));
    }

    public static <I> ItemNeighborhood<I> cached(ItemNeighborhood<I> neighborhood) {
        return new ItemNeighborhood<I>(neighborhood, new CachedNeighborhood(neighborhood.numItems(), neighborhood.neighborhood()));
    }

    public static <I> ItemNeighborhood<I> cached(FastItemIndex<I> items, Stream<Tuple2<I, Stream<Tuple2od<I>>>> neighborhoods) {
        return new ItemNeighborhood<I>(items, new CachedNeighborhood(items.numItems(), neighborhoods
                .map(t -> tuple(items.item2iidx(t.v1), t.v2.map(items::item2iidx)))));
    }

}
