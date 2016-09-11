package org.ranksys.recommenders.nn.user.neighborhood;

import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.recommenders.nn.neighborhood.CachedNeighborhood;
import org.ranksys.recommenders.nn.neighborhood.ThresholdNeighborhood;
import org.ranksys.recommenders.nn.neighborhood.TopKNeighborhood;
import org.ranksys.recommenders.nn.user.sim.UserSimilarity;

public class UserNeighborhoods {

    public static <U> UserNeighborhood<U> topK(UserSimilarity<U> similarity, int k) {
        return new UserNeighborhood<>(similarity, new TopKNeighborhood(similarity.similarity(), k));
    }

    public static <U> UserNeighborhood<U> threshold(UserSimilarity<U> similarity, double threshold) {
        return new UserNeighborhood<>(similarity, new ThresholdNeighborhood(similarity.similarity(), threshold));
    }

    public static <U> UserNeighborhood<U> cached(UserNeighborhood<U> neighborhood) {
        return new UserNeighborhood<>(neighborhood, new CachedNeighborhood(neighborhood.numUsers(), neighborhood.neighborhood()));
    }

    public static <U> UserNeighborhood<U> cached(FastUserIndex<U> users, Stream<Tuple2<U, Stream<Tuple2od<U>>>> neighborhoods) {
        return new UserNeighborhood<>(users, new CachedNeighborhood(users.numUsers(), neighborhoods
                .map(t -> tuple(users.user2uidx(t.v1), t.v2.map(users::user2uidx)))));
    }
}
