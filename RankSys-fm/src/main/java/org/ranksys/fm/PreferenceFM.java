/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import java.util.stream.Stream;
import org.ranksys.javafm.FM;
import org.ranksys.javafm.FMInstance;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class PreferenceFM<U, I> implements FastUserIndex<U>, FastItemIndex<I> {

    private final FastUserIndex<U> ui;
    private final FastItemIndex<I> ii;
    private final FM fm;

    public PreferenceFM(FastUserIndex<U> users, FastItemIndex<I> items, FM fm) {
        this.ui = users;
        this.ii = items;
        this.fm = fm;
    }

    public FM getFM() {
        return fm;
    }

    public double predict(U u, IdPref<I> pref) {
        return predict(user2uidx(u), new IdxPref(item2iidx(pref.v1), pref.v2));
    }

    public double predict(int uidx, IdxPref pref) {
        int[] k = {uidx, pref.v1 + numUsers()};
        double[] v = {1.0, 1.0};

        return fm.predict(new FMInstance(pref.v2, k, v));
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
