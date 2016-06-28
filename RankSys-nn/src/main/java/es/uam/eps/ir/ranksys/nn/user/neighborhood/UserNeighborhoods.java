package es.uam.eps.ir.ranksys.nn.user.neighborhood;

import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.ThresholdNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.TopKNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.sim.UserSimilarity;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.stream.Stream;

import static org.ranksys.core.util.tuples.Tuples.tuple;

public class UserNeighborhoods {

    public static <U> UserNeighborhood<U> topK(UserSimilarity<U> similarity, int k) {
        return new UserNeighborhood<U>(similarity, new TopKNeighborhood(similarity.similarity(), k));
    }

    public static <U> UserNeighborhood<U> threshold(UserSimilarity<U> similarity, double threshold) {
        return new UserNeighborhood<U>(similarity, new ThresholdNeighborhood(similarity.similarity(), threshold));
    }

    public static <U> UserNeighborhood<U> cached(UserNeighborhood<U> neighborhood) {
        return new UserNeighborhood<U>(neighborhood, new CachedNeighborhood(neighborhood.numUsers(), neighborhood.neighborhood()));
    }

    public static <U> UserNeighborhood<U> cached(FastUserIndex<U> users, Stream<Tuple2<U, Stream<Tuple2od<U>>>> neighborhoods) {
        return new UserNeighborhood<U>(users, new CachedNeighborhood(users.numUsers(), neighborhoods
                .map(t -> tuple(users.user2uidx(t.v1), t.v2.map(users::user2uidx)))));
    }
}
