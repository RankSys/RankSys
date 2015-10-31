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
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public abstract class AbstractCODECPreferenceData<U, I, Cu, Ci> extends AbstractFastPreferenceData<U, I> implements FasterPreferenceData<U, I> {

    protected final CODEC<Cu> u_codec;
    protected final CODEC<Ci> i_codec;
    protected final Cu[] u_idxs;
    protected final int[] u_len;
    protected final Ci[] i_idxs;
    protected final int[] i_len;
    protected final int numPreferences;

    public AbstractCODECPreferenceData(Cu[] u_idxs, int[] u_len, Ci[] i_idxs, int[] i_len, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) {
        super(users, items);
        this.u_codec = u_codec;
        this.i_codec = i_codec;
        this.u_idxs = u_idxs;
        this.u_len = u_len;
        this.i_idxs = i_idxs;
        this.i_len = i_len;
        this.numPreferences = of(u_len).sum();
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
        return numPreferences;
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
