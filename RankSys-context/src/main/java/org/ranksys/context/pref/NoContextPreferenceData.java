/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
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
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.core.preference.PointWisePreferenceData;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class NoContextPreferenceData<U, I, C> implements ContextPreferenceData<U, I, C> {

    protected final PointWisePreferenceData<U, I> d;
    protected final C emptyCtx;

    public NoContextPreferenceData(PointWisePreferenceData<U, I> preferences, C emptyCtx) {
        this.d = preferences;
        this.emptyCtx = emptyCtx;
    }

    @Override
    public Stream<IdPrefCtx<I, C>> getUserPreferences(U u) {
        return d.getUserPreferences(u).map(p -> new IdPrefCtx<>(p.id, p.v, Arrays.asList(emptyCtx)));
    }

    @Override
    public Stream<IdPrefCtx<U, C>> getItemPreferences(I i) {
        return d.getItemPreferences(i).map(p -> new IdPrefCtx<>(p.id, p.v, Arrays.asList(emptyCtx)));
    }

    @Override
    public IdPrefCtx<I, C> getPreference(U u, I i) {
        IdPref<I> p = d.getPreference(u, i);
        if (p == null) {
            return null;
        } else {
            return new IdPrefCtx<>(p.id, p.v, Arrays.asList(emptyCtx));
        }
    }

    @Override
    public int numUsersWithPreferences() {
        return d.numUsersWithPreferences();
    }

    @Override
    public int numItemsWithPreferences() {
        return d.numItemsWithPreferences();
    }

    @Override
    public int numUsers(I i) {
        return d.numUsers(i);
    }

    @Override
    public int numItems(U u) {
        return d.numItems(u);
    }

    @Override
    public int numPreferences() {
        return d.numPreferences();
    }

    @Override
    public Stream<U> getUsersWithPreferences() {
        return d.getUsersWithPreferences();
    }

    @Override
    public Stream<I> getItemsWithPreferences() {
        return d.getItemsWithPreferences();
    }

    @Override
    public boolean containsUser(U u) {
        return d.containsUser(u);
    }

    @Override
    public int numUsers() {
        return d.numUsers();
    }

    @Override
    public Stream<U> getAllUsers() {
        return d.getAllUsers();
    }

    @Override
    public boolean containsItem(I i) {
        return d.containsItem(i);
    }

    @Override
    public int numItems() {
        return d.numItems();
    }

    @Override
    public Stream<I> getAllItems() {
        return d.getAllItems();
    }

    @Override
    public int getContextSize() {
        return 0;
    }

}
