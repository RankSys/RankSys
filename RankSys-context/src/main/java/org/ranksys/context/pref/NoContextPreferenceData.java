/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.ranksys.core.preference.PointWisePreferenceData;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
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
    public Optional<IdPrefCtx<I, C>> getPreference(U u, I i) {
        Optional<? extends IdPref<I>> p = d.getPreference(u, i);
        if (!p.isPresent()) {
            return Optional.empty();
        } else {
            return Optional.of(new IdPrefCtx<>(p.get().id, p.get().v, Arrays.asList(emptyCtx)));
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
