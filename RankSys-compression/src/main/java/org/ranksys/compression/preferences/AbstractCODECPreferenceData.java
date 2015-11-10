package org.ranksys.compression.preferences;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.FasterPreferenceData;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import java.util.stream.IntStream;
import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.range;
import org.ranksys.compression.codecs.CODEC;
import static org.ranksys.compression.util.Delta.atled;
import org.ranksys.core.util.iterators.ArrayIntIterator;

/**
 * Abstract PreferenceData using compression.
 * <br>
 * If you use this code, please cite the following papers:
 * <ul>
 * <li>Vargas, S., Macdonald, C., Ounis, I. (2015). Analysing Compression Techniques for In-Memory Collaborative Filtering. In Poster Proceedings of the 9th ACM Conference on Recommender Systems. <a href="http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf">http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf</a>.</li>
 * <li>Catena, M., Macdonald, C., Ounis, I. (2014). On Inverted Index Compression for Search Engine Efficiency. In ECIR (pp. 359–371). doi:10.1007/978-3-319-06028-6_30</li>
 * </ul>
 * The code that reproduces the results of the RecSys 2015 poster by Vargas et al. in a separated project: <a href="http://github.com/saulvargas/recsys2015">http://github.com/saulvargas/recsys2015</a>
 * <br>
 * The search index compression technologies of the ECIR paper by Catena et al. is part of the Terrier IR Platform: <a href="http://terrier.org/docs/v4.0/compression.html">http://terrier.org/docs/v4.0/compression.html</a>.
 *
 * @param <U> type of users
 * @param <I> type of items
 * @param <Cu> coding for user identifiers
 * @param <Ci> coding for item identifiers
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public abstract class AbstractCODECPreferenceData<U, I, Cu, Ci> extends AbstractFastPreferenceData<U, I> implements FasterPreferenceData<U, I> {

    /**
     * CODEC for user preferences.
     */
    protected final CODEC<Cu> u_codec;

    /**
     * CODEC for item preferences.
     */
    protected final CODEC<Ci> i_codec;

    /**
     * lists of items for users.
     */
    protected final Cu[] u_idxs;

    /**
     * lengths of user preferences lists.
     */
    protected final int[] u_len;

    /**
     * list of users for items.
     */
    protected final Ci[] i_idxs;

    /**
     * lengths of item preferences lists.
     */
    protected final int[] i_len;

    /**
     * Constructor.
     *
     * @param users user index
     * @param items item index
     * @param u_codec user preferences CODEC
     * @param i_codec item preferences CODEC
     */
    @SuppressWarnings("unchecked")
    public AbstractCODECPreferenceData(FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) {
        super(users, items);
        this.u_codec = u_codec;
        this.i_codec = i_codec;
        this.u_idxs = (Cu[]) new Object[users.numUsers()];
        this.u_len = new int[users.numUsers()];
        this.i_idxs = (Ci[]) new Object[items.numItems()];
        this.i_len = new int[items.numItems()];
    }

    @Override
    public int numUsers(int iidx) {
        return i_len[iidx];
    }

    @Override
    public int numItems(int uidx) {
        return u_len[uidx];
    }

    @Override
    public int numPreferences() {
        return of(u_len).sum();
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return range(0, u_len.length).filter(uidx -> u_len[uidx] > 0);
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return range(0, i_len.length).filter(iidx -> i_len[iidx] > 0);
    }

    @Override
    public IntIterator getUidxIidxs(final int uidx) {
        return getIdx(u_idxs[uidx], u_len[uidx], u_codec);
    }

    @Override
    public IntIterator getIidxUidxs(final int iidx) {
        return getIdx(i_idxs[iidx], i_len[iidx], i_codec);
    }

    private static <Cx> IntIterator getIdx(Cx cidxs, int len, CODEC<Cx> x_codec) {
        if (len == 0) {
            return IntIterators.EMPTY_ITERATOR;
        }
        int[] idxs = new int[len];
        x_codec.dec(cidxs, idxs, 0, len);
        if (!x_codec.isIntegrated()) {
            atled(idxs, 0, len);
        }
        return new ArrayIntIterator(idxs);
    }

}
