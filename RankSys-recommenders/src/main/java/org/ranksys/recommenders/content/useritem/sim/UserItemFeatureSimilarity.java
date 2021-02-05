package org.ranksys.recommenders.content.useritem.sim;

import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2od;
import org.ranksys.recommenders.content.sim.FeatureSimilarity;
import org.ranksys.recommenders.nn.sim.Similarity;

import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class UserItemFeatureSimilarity<U,I> implements FastUserIndex<U>, FastItemIndex<I>
{
    private final FastUserIndex<U> uIndex;
    private final FastItemIndex<I> iIndex;
    private final FeatureSimilarity sim;

    /**
     * Constructor.
     * @param uIndex fast user index.
     * @param iIndex fast item index.
     * @param sim fast feature similarity.
     */
    public UserItemFeatureSimilarity(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, FeatureSimilarity sim)
    {
        this.uIndex = uIndex;
        this.iIndex = iIndex;
        this.sim = sim;
    }
    @Override
    public int item2iidx(I i)
    {
        return this.iIndex.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx)
    {
        return this.iIndex.iidx2item(iidx);
    }

    @Override
    public int numItems()
    {
        return this.iIndex.numItems();
    }

    @Override
    public int user2uidx(U u)
    {
        return this.uIndex.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx)
    {
        return this.uIndex.uidx2user(uidx);
    }

    @Override
    public int numUsers()
    {
        return this.uIndex.numUsers();
    }

    public Similarity similarity() {
        return sim;
    }


    /**
     * Returns a function returning similarities between the user and the items
     *
     * @param u user
     * @return a function returning similarities between the user and the items
     */
    public ToDoubleFunction<I> similarity(U u)
    {
        return i -> sim.similarity(user2uidx(u)).applyAsDouble(item2iidx(i));
    }

    /**
     * Returns the similarity between a user and an item.
     *
     * @param u the user
     * @param i the item
     * @return similarity value between the user and the item
     */
    public double similarity(U u, I i) {
        return sim.similarity(user2uidx(u), item2iidx(i));
    }

    /**
     * Returns all the items that are similar to the user.
     *
     * @param u item
     * @return a stream of item-similarity pairs
     */
    public Stream<Tuple2od<I>> similarItems(U u) {
        return similarItems(user2uidx(u))
                .map(this::iidx2item);
    }



    /**
     * Returns all the items that are similar to the item - fast version.
     *
     * @param uidx user
     * @return a stream of item-similarity pairs
     */
    public Stream<Tuple2id> similarItems(int uidx) {
        return sim.similarElems(uidx);
    }


}
