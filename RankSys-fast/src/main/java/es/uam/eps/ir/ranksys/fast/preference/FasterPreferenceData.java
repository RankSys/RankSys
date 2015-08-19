package es.uam.eps.ir.ranksys.fast.preference;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;

/**
 * Faster preference data, which returns separately user/item idxs and values
 * for even faster performance.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface FasterPreferenceData<U, I> extends FastPreferenceData<U, I> {

    /**
     * Returns the item idxs of the preferences of a user.
     *
     * @param uidx user index
     * @return iterator of the idxs of the items
     */
    public IntIterator getUidxIidxs(final int uidx);

    /**
     * Returns the item values of the preferences of a user.
     *
     * @param uidx user index
     * @return iterator of the values of the items
     */
    public DoubleIterator getUidxVs(final int uidx);

    /**
     * Returns the user idxs of the preferences for an item.
     *
     * @param iidx item index
     * @return iterator of the idxs of the users.
     */
    public IntIterator getIidxUidxs(final int iidx);

    /**
     * Returns the user values of the preferences for an item.
     *
     * @param iidx item index
     * @return iterator of the values of the users
     */
    public DoubleIterator getIidxVs(final int iidx);

}
