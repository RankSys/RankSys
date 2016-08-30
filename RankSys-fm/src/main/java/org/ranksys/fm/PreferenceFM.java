/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm;

import org.ranksys.fast.index.FastItemIndex;
import org.ranksys.fast.index.FastUserIndex;
import org.ranksys.fast.preference.IdxPref;
import java.util.function.Function;
import java.util.stream.Stream;
import org.ranksys.core.preference.IdPref;
import org.ranksys.javafm.FM;
import org.ranksys.javafm.FMInstance;

/**
 * Wraps a factorisation machine to work with RankSys user-preference pairs.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> type of users
 * @param <I> type of items
 */
public class PreferenceFM<U, I> implements FastUserIndex<U>, FastItemIndex<I> {

    private static final double[] UI_VALUES = {1.0, 1.0};

    private final FastUserIndex<U> ui;
    private final FastItemIndex<I> ii;
    private final FM fm;
    private final Function<IdPref<I>, IdxPref> uPrefFun;

    /**
     * Constructor with default converter to IdxPref.
     *
     * @param users user index
     * @param items item index
     * @param fm factorisation machine
     */
    public PreferenceFM(FastUserIndex<U> users, FastItemIndex<I> items, FM fm) {
        this(users, items, fm, p -> new IdxPref(items.item2iidx(p)));
    }

    /**
     * Constructor with custom default converter to IdxPref.
     *
     * @param users user index
     * @param items item index
     * @param fm factorisation machine
     * @param uPrefFun converter to IdxPref
     */
    public PreferenceFM(FastUserIndex<U> users, FastItemIndex<I> items, FM fm, Function<IdPref<I>, IdxPref> uPrefFun) {
        this.ui = users;
        this.ii = items;
        this.fm = fm;
        this.uPrefFun = uPrefFun;
    }

    /**
     * Returns the enclosed factorisation machine.
     *
     * @return factorisation machine
     */
    public FM getFM() {
        return fm;
    }

    /**
     * Predicts the preference by a user to an item preference.
     *
     * @param u user
     * @param pref preference
     * @return predicted score
     */
    public double predict(U u, IdPref<I> pref) {
        return predict(user2uidx(u), uPrefFun.apply(pref));
    }

    /**
     * Predicts the preference by a user to an item preference (fast version).
     *
     * @param uidx user
     * @param pref preference
     * @return predicted score
     */
    public double predict(int uidx, IdxPref pref) {
        return fm.predict(new FMInstance(pref.v2, new int[]{uidx, pref.v1 + numUsers()}, UI_VALUES));
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

}
