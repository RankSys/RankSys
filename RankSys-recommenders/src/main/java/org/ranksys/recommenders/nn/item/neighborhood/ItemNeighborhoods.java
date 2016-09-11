package org.ranksys.recommenders.nn.item.neighborhood;

import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.recommenders.nn.item.sim.ItemSimilarity;
import org.ranksys.recommenders.nn.neighborhood.CachedNeighborhood;
import org.ranksys.recommenders.nn.neighborhood.ThresholdNeighborhood;
import org.ranksys.recommenders.nn.neighborhood.TopKNeighborhood;

public class ItemNeighborhoods {

    public static <I> ItemNeighborhood<I> topK(ItemSimilarity<I> similarity, int k) {
        return new ItemNeighborhood<>(similarity, new TopKNeighborhood(similarity.similarity(), k));
    }

    public static <I> ItemNeighborhood<I> threshold(ItemSimilarity<I> similarity, double threshold) {
        return new ItemNeighborhood<>(similarity, new ThresholdNeighborhood(similarity.similarity(), threshold));
    }

    public static <I> ItemNeighborhood<I> cached(ItemNeighborhood<I> neighborhood) {
        return new ItemNeighborhood<>(neighborhood, new CachedNeighborhood(neighborhood.numItems(), neighborhood.neighborhood()));
    }

    public static <I> ItemNeighborhood<I> cached(FastItemIndex<I> items, Stream<Tuple2<I, Stream<Tuple2od<I>>>> neighborhoods) {
        return new ItemNeighborhood<>(items, new CachedNeighborhood(items.numItems(), neighborhoods
                .map(t -> tuple(items.item2iidx(t.v1), t.v2.map(items::item2iidx)))));
    }

}
