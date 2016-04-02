/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;

/**
 * Abstract fast preference sampler, implementing methods from
 * FastUserIndex and FastItemIndex.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <U> user type
 * @param <I> item type
 */
public abstract class AbstractFastPreferenceSampler<U, I> implements FastPreferenceSampler<U, I> {

    private final FastUserIndex<U> users;
    private final FastItemIndex<I> items;

    /**
     * Constructor.
     *
     * @param users user index
     * @param items item index
     */
    public AbstractFastPreferenceSampler(FastUserIndex<U> users, FastItemIndex<I> items) {
        this.users = users;
        this.items = items;
    }

    @Override
    public int numUsers() {
        return users.numUsers();
    }

    @Override
    public int numItems() {
        return items.numItems();
    }

    @Override
    public int user2uidx(U u) {
        return users.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return users.uidx2user(uidx);
    }

    @Override
    public int item2iidx(I i) {
        return items.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return items.iidx2item(iidx);
    }
}
