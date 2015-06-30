/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import java.util.stream.Stream;

/**
 * Abstract FastFeatureData, implementing the interfaces of FastUserIndex and
 * FastItemIndex by delegating to implementations of these.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <O> type of other information for users and items
 */
public abstract class AbstractFastPreferenceData<U, I> implements FastPreferenceData<U, I> {

    private final FastUserIndex<U> ui;
    private final FastItemIndex<I> ii;

    /**
     * Constructor.
     *
     * @param userIndex user index
     * @param itemIndex item index
     */
    public AbstractFastPreferenceData(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex) {
        this.ui = userIndex;
        this.ii = itemIndex;
    }

    @Override
    public int user2uidx(U u) {
        return ui.user2uidx(u);
    }

    @Override
    public U uidx2user(int i) {
        return ui.uidx2user(i);
    }

    @Override
    public boolean containsUser(U u) {
        return ui.containsUser(u);
    }

    @Override
    public int numUsers() {
        return ui.numUsers();
    }

    @Override
    public Stream<U> getAllUsers() {
        return ui.getAllUsers();
    }

    @Override
    public int item2iidx(I i) {
        return ii.item2iidx(i);
    }

    @Override
    public I iidx2item(int i) {
        return ii.iidx2item(i);
    }

    @Override
    public boolean containsItem(I i) {
        return ii.containsItem(i);
    }

    @Override
    public int numItems() {
        return ii.numItems();
    }

    @Override
    public Stream<I> getAllItems() {
        return ii.getAllItems();
    }

    @Override
    public int numUsers(I i) {
        return numUsers(item2iidx(i));
    }

    @Override
    public int numItems(U u) {
        return numItems(user2uidx(u));
    }

    @Override
    public Stream<IdPref<I>> getUserPreferences(final U u) {
        return getUidxPreferences(user2uidx(u)).map(iv -> new IdPref<>(iidx2item(iv.idx), iv.v));
    }

    @Override
    public Stream<IdPref<U>> getItemPreferences(final I i) {
        return getIidxPreferences(item2iidx(i)).map(uv -> new IdPref<>(uidx2user(uv.idx), uv.v));
    }

    @Override
    public Stream<U> getUsersWithPreferences() {
        return getUidxWithPreferences().mapToObj(this::uidx2user);
    }

    @Override
    public Stream<I> getItemsWithPreferences() {
        return getIidxWithPreferences().mapToObj(this::iidx2item);
    }

    @Override
    public int numUsersWithPreferences() {
        return (int) getUidxWithPreferences().count();
    }

    @Override
    public int numItemsWithPreferences() {
        return (int) getIidxWithPreferences().count();
    }

}
