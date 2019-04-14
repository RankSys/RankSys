/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.preference.fast.updateable;


import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.core.index.fast.updateable.FastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.FastUpdateableUserIndex;
import org.ranksys.core.preference.IdPref;
import org.ranksys.core.preference.fast.AbstractFastPreferenceData;
import org.ranksys.core.preference.fast.IdxPref;

/**
 * Abstract updateable fast preference data, implementing the FastUpdateablePreferenceData interface.
 * 
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractFastUpdateablePreferenceData<U, I> extends AbstractFastPreferenceData<U,I> implements FastUpdateablePreferenceData<U,I>
{
    /**
     * Constructor.
     *
     * @param users user index
     * @param items item index
     */
    public AbstractFastUpdateablePreferenceData(FastUpdateableUserIndex<U> users, FastUpdateableItemIndex<I> items) 
    {
        this(users, items,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(items.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(users.uidx2user(p)));
    }

    /**
     * Constructor.
     *
     * @param userIndex user index
     * @param itemIndex item index
     * @param uPrefFun converter from IdxPref to IdPref (preference for item).
     * @param iPrefFun converter from IdxPref to IdPref (preference from user).
     */
    public AbstractFastUpdateablePreferenceData(FastUpdateableUserIndex<U> userIndex, FastUpdateableItemIndex<I> itemIndex, Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) 
    {
        super(userIndex, itemIndex, uPrefFun, iPrefFun);
    }

    @Override
    public void updateAddUser(U u)
    {
        this.addUser(u);
    }
    
    @Override
    public void updateAddItem(I i)
    {
        this.addItem(i);
    }
    
    /*@Override
    public void updateRemoveUser(U u)
    {
        this.removeUser(u);
    }
    
    @Override
    public void updateRemoveItem(I i)
    {
        this.removeItem(i);
    }*/
    
    @Override
    public void update(Stream<Tuple3<U,I,Double>> tuples)
    {
        tuples.forEach(t -> 
        {
            if(this.containsUser(t.v1) && this.containsItem(t.v2))
            {
                int uidx = this.user2uidx(t.v1);
                int iidx = this.item2iidx(t.v2);
                this.updateRating(uidx, iidx, t.v3);
            }
        });
    }

    @Override
    public void update(U u, I i, double val)
    {
        if(this.containsUser(u) && this.containsItem(i))
        {
            int uidx = this.user2uidx(u);
            int iidx = this.item2iidx(i);
            this.updateRating(uidx, iidx, val); 
        }
    }
    
    @Override
    public void updateDelete(Stream<Tuple2<U,I>> tuples)
    {
        tuples.forEach(t -> 
        {
            if(this.containsUser(t.v1) && this.containsItem(t.v2))
            {
                int uidx = this.user2uidx(t.v1());
                int iidx = this.item2iidx(t.v2());

                if(uidx >= 0 && iidx >= 0)
                {
                    this.updateDelete(uidx, iidx);
                }
            }
        });
    }
    
    @Override
    public void updateDelete(U u, I i)
    {
        int uidx = this.user2uidx(u);
        int iidx = this.item2iidx(i);
            
        if(uidx >= 0 && iidx >= 0)
        {
            this.updateDelete(uidx, iidx);
        }
    }
    
    /**
     * Updates a rating value.
     * @param uidx identifier of the user
     * @param iidx identifier of the item
     * @param rating the rating.
     */
    protected abstract void updateRating(int uidx, int iidx, double rating);
    
    /**
     * Deletes a rating.
     * @param uidx identifier of the user.
     * @param iidx identifier of the item.
     */
    protected abstract void updateDelete(int uidx, int iidx);
}
