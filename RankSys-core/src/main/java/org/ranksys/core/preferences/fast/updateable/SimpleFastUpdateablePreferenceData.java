/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.preferences.fast.updateable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.util.Comparator.comparingInt;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jooq.lambda.function.Function4;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.ranksys.core.index.fast.updateable.FastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.FastUpdateableUserIndex;
import org.ranksys.core.preference.IdPref;
import org.ranksys.core.preference.fast.IdxPref;

/**
 * Simple implementation of FastPreferenceData backed by nested lists.
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class SimpleFastUpdateablePreferenceData<U, I> extends StreamsAbstractFastUpdateablePreferenceData<U, I> implements FastUpdateablePointWisePreferenceData<U, I>, Serializable 
{
    /**
     * Current number of preferences.
     */
    private int numPreferences;
    /**
     * User preferences
     */
    private final List<List<IdxPref>> uidxList;
    /**
     * Item preferences
     */
    private final List<List<IdxPref>> iidxList; 
    
    /**
     * Constructor with default IdxPref to IdPref converter.
     *
     * @param numPreferences initial number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     */
    protected SimpleFastUpdateablePreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList,
            FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex) 
    {
        this(numPreferences, uidxList, iidxList, uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    /**
     * Constructor with custom IdxPref to IdPref converter.
     *
     * @param numPreferences initial number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     * @param uPrefFun user IdxPref to IdPref converter
     * @param iPrefFun item IdxPref to IdPref converter
     */
    protected SimpleFastUpdateablePreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList,
            FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) 
    {
        super(uIndex, iIndex, uPrefFun, iPrefFun);
        this.uidxList = uidxList;
        this.iidxList = iidxList;
        this.numPreferences = numPreferences;
        uidxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
        iidxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
    }

    @Override
    public int numUsers(int iidx) 
    {
        if (iidxList.get(iidx) == null) 
        {
            return 0;
        }
        return iidxList.get(iidx).size();
    }

    @Override
    public int numItems(int uidx) 
    {
        if (uidxList.get(uidx) == null) 
        {
            return 0;
        }
        return uidxList.get(uidx).size();
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(int uidx) 
    {
        if (uidxList.get(uidx) == null) 
        {
            return Stream.empty();
        } 
        else 
        {
            return uidxList.get(uidx).stream();
        }
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(int iidx) 
    {
        if (iidxList.get(iidx) == null) 
        {
            return Stream.empty();
        } 
        else 
        {
            return iidxList.get(iidx).stream();
        }
    }

    @Override
    public int numPreferences() 
    {
        return numPreferences;
    }

    @Override
    public IntStream getUidxWithPreferences() 
    {
        return IntStream.range(0, numUsers())
                        .filter(uidx -> uidxList.get(uidx) != null);
    }

    @Override
    public IntStream getIidxWithPreferences() 
    {
        return IntStream.range(0, this.numItems())
                        .filter(iidx -> iidxList.get(iidx) != null);
    }

    @Override
    public int numUsersWithPreferences() 
    {
        return (int) uidxList.stream()
                             .filter(iv -> iv != null)
                             .count();
    }

    @Override
    public int numItemsWithPreferences() 
    {
        return (int) iidxList.stream()
                             .filter(iv -> iv != null)
                             .count();
    }

    @Override
    public Optional<IdxPref> getPreference(int uidx, int iidx) 
    {
        List<IdxPref> uList = uidxList.get(uidx);
        if(uList == null) return Optional.empty();
        Comparator<IdxPref> comp = (x,y) -> x.v1 - y.v1;
        int position = Collections.binarySearch(uList, new IdxPref(iidx, 1.0), comp);
        
        if(position >= 0)
        {
            return Optional.of(uList.get(position));
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<? extends IdPref<I>> getPreference(U u, I i) 
    {
        if(this.containsUser(u) && this.containsItem(i))
        {
            Optional<? extends IdxPref> pref = getPreference(user2uidx(u), item2iidx(i));

            if (!pref.isPresent()) 
            {
                return Optional.empty();
            } 
            else 
            {
                return Optional.of(uPrefFun.apply(pref.get()));
            }
        }
        else
        {
            return Optional.empty();
        }
    }
    
    @Override
    public int addUser(U u) 
    {
        int uidx = ((FastUpdateableUserIndex<U>)this.ui).addUser(u);
        if(this.uidxList.size() == uidx) // If the user is really new
        {
            this.uidxList.add(null);
        }
        return uidx;
    }

    @Override
    public int addItem(I i) 
    {
        int iidx = ((FastUpdateableItemIndex<I>)this.ii).addItem(i);
        if(this.iidxList.size() == iidx) // If the item is really new
        {
            this.iidxList.add(null);
        }
        return iidx;
    }
    
    /*@Override
    public int removeUser(U u)
    {
        // First, check if the user exists.
        int uidx = this.user2uidx(u);
        if(uidx == -1)
        {
            return -1;
        }
        
        // Then, we want to remove uidx from the lists of the items which u has rated.
        if(this.uidxList.get(uidx) != null)
        {
            int removePref = this.uidxList.get(uidx).size();
            this.uidxList.get(uidx).forEach(iidx -> 
            {
                List<IdxPref> list = this.iidxList.get(iidx.v1);
                this.updateDelete(uidx, list);
            });
            this.numPreferences = this.numPreferences - removePref;
        }
        
        // We have to update the identifier for the rest of the users. We run over
        // the whole set of items, and, if they have a user index greater than uidx
        // in their preferences lists, we reduce the identifier by a unit.
        // OBS: The order will be kept by doing this.
        this.getAllIidx().forEach(iidx -> 
        {
            if(this.iidxList.get(iidx) != null)
            {
                List<IdxPref> list = this.iidxList.get(iidx);
                int n = list.size();
                IntStream.range(0, n).forEach(i -> 
                {
                    int auxidx = list.get(i).v1;
                    double value = list.get(i).v2;
                    if(list.get(i).v1 > uidx)
                    {
                        list.set(i, new IdxPref(auxidx-1, value));
                    }
                });
            }
        });
        
        // Remove the user from the uidxlist, and from the index.
        this.uidxList.remove(uidx);
        return ((FastUpdateableUserIndex<U>) this.ui).removeUser(u);
    } */

    /*@Override
    public int removeItem(I i)
    {
        // First, check if the item exists.
        int iidx = this.item2iidx(i);
        if(iidx == -1)
        {
            return -1;
        }
        
        // Then, we want to remove iidx from the lists of the users which have rated the item.
        if(this.iidxList.get(iidx) != null)
        {
            int removePref = this.iidxList.get(iidx).size();
            this.iidxList.get(iidx).forEach(uidx -> 
            {
                List<IdxPref> list = this.uidxList.get(uidx.v1);
                this.updateDelete(iidx, list);
            });
            this.numPreferences = this.numPreferences - removePref;
        }
        
        // We have to update the identifier for the rest of the items. We run over
        // the whole set of users, and, if they have a item index greater than iidx
        // in their preferences lists, we reduce the identifier by a unit.
        // OBS: The order will be kept by doing this.
        this.getAllUidx().forEach(uidx -> 
        {
            if(this.uidxList.get(uidx) != null)
            {
                List<IdxPref> list = this.uidxList.get(uidx);
                int n = list.size();
                IntStream.range(0, n).forEach(u -> 
                {
                    int auxidx = list.get(u).v1;
                    double value = list.get(u).v2;
                    if(list.get(u).v1 > iidx)
                    {
                        list.set(u, new IdxPref(auxidx-1, value));
                    }
                });
            }
        });
        
        // Remove the item from the iidxlist, and from the index.
        this.iidxList.remove(iidx);
        return ((FastUpdateableItemIndex<I>) this.ii).removeItem(i);       
    }   */
    
    @Override
    public void updateRating(int uidx, int iidx, double rating) 
    {        
        // If the user or the item are not in the preference data, do nothing.
        if(uidx < 0 || this.uidxList.size() <= uidx || iidx < 0 || this.iidxList.size() <= iidx)
        {
            return;
        }
        
        // Update the value for the user.
        if(this.uidxList.get(uidx) == null) // If the user does not have preferences.
        {
            List<IdxPref> idxPrefList = new ArrayList<>();
            idxPrefList.add(new IdxPref(iidx, rating));
            this.uidxList.set(uidx, idxPrefList);
            this.numPreferences++;
        }
        else // if the user has at least one preference
        {
            // Update the preference for the user.
            boolean addPref = this.updatePreference(iidx, rating, this.uidxList.get(uidx));
            if(addPref) this.numPreferences++;
        }
        
        // Update the value for the item.
        if(this.iidxList.get(iidx) == null) // If the item does not have ratings.
        {
            List<IdxPref> idxPrefList = new ArrayList<>();
            idxPrefList.add(new IdxPref(uidx, rating));
            this.iidxList.set(iidx,idxPrefList);
        }
        else // if the item has been rated by at least one user.
        {
            this.updatePreference(uidx, rating, this.iidxList.get(iidx));
        }    
    }
    
    /**
     * Updates a preference.
     * @param idx the identifier of the preference to add.
     * @param value the rating value.
     * @param list the list in which we want to update the preference.
     * @return true if the rating was added, false if it was just updated.
     */
    private boolean updatePreference(int idx, double value, List<IdxPref> list)
    {
        if(list.size() == this.numItems())
        {
            System.err.print("");
        }
        IdxPref newIdx = new IdxPref(idx, value);
    
        // Use binary search to find the rating.
        Comparator<IdxPref> comp = (x,y) -> x.v1 - y.v1;
        int position = Collections.binarySearch(list, newIdx, comp);
        
        if(position < 0) // the rating does not exist.
        {
            position = Math.abs(position+1);
            list.add(position, newIdx);
            return true;
        }
        else // the rating did already exist
        {
            list.set(position, newIdx);
            return false;
        }
    }   

    @Override
    protected void updateDelete(int uidx, int iidx)
    {
        // If the user or the item are not in the preference data, do nothing.
        if(uidx < 0 || this.uidxList.size() <= uidx || iidx < 0 || this.iidxList.size() <= iidx)
        {
            return;
        }
        
        // First, delete from the uidxList
        if(this.updateDelete(iidx, this.uidxList.get(uidx)))
        {
            // Then, delete from the iidxList
            this.updateDelete(uidx, this.iidxList.get(iidx));
            this.numPreferences--;
        }
    }
    
    /**
     * Deletes a rating from the data.
     * @param idx identifier of the element to delete
     * @param list list from where the element has to be removed
     * @return true if the element was removed, false otherwise.
     */
    private boolean updateDelete(int idx, List<IdxPref> list) 
    {
        // If the list is empty, do nothing
        if(list == null) return false;
        
        // Search for the position of the element to remove
        IdxPref newIdx = new IdxPref(idx, 1.0);
        Comparator<IdxPref> comp = (x,y) -> x.v1 - y.v1;
        int position = Collections.binarySearch(list, newIdx, comp);
        
        // If it exists
        if(position >= 0)
        {
            list.remove(position);
            return true;
        }
        
        return false;
    }
    
    /**
     * Loads a SimpleFastPreferenceData from a stream of user-item-value triples.
     *
     * @param <U> user type
     * @param <I> item type
     * @param tuples stream of user-item-value triples
     * @param uIndex user index
     * @param iIndex item index
     * @return an instance of SimpleFastPreferenceData containing the data from the input stream
     */
    public static <U, I> SimpleFastUpdateablePreferenceData<U, I> load(Stream<Tuple3<U, I, Double>> tuples, FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex) 
    {
        return load(tuples.map(t -> t.concat((Void) null)),
                (uidx, iidx, v, o) -> new IdxPref(iidx, v),
                (uidx, iidx, v, o) -> new IdxPref(uidx, v),
                uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    /**
     * Loads a SimpleFastPreferenceData from a stream of user-item-value-other tuples. It can accomodate other information, thus you need to provide sub-classes of IdxPref IdPref accomodating for this new information.
     *
     * @param <U> user type
     * @param <I> item type
     * @param <O> additional information type
     * @param tuples stream of user-item-value-other tuples
     * @param uIdxPrefFun converts a tuple to a user IdxPref
     * @param iIdxPrefFun converts a tuple to a item IdxPref
     * @param uIndex user index
     * @param iIndex item index
     * @param uIdPrefFun user IdxPref to IdPref converter
     * @param iIdPrefFun item IdxPref to IdPref converter
     * @return an instance of SimpleFastPreferenceData containing the data from the input stream
     */
    public static <U, I, O> SimpleFastUpdateablePreferenceData<U, I> load(Stream<Tuple4<U, I, Double, O>> tuples,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> uIdxPrefFun,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> iIdxPrefFun,
            FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uIdPrefFun,
            Function<IdxPref, IdPref<U>> iIdPrefFun) 
    {
        AtomicInteger numPreferences = new AtomicInteger();

        List<List<IdxPref>> uidxList = new ArrayList<>();
        for (int uidx = 0; uidx < uIndex.numUsers(); uidx++) {
            uidxList.add(null);
        }

        List<List<IdxPref>> iidxList = new ArrayList<>();
        for (int iidx = 0; iidx < iIndex.numItems(); iidx++) {
            iidxList.add(null);
        }

        tuples.forEach(t -> {
            int uidx = uIndex.user2uidx(t.v1);
            int iidx = iIndex.item2iidx(t.v2);

            numPreferences.incrementAndGet();

            List<IdxPref> uList = uidxList.get(uidx);
            if (uList == null) {
                uList = new ArrayList<>();
                uidxList.set(uidx, uList);
            }
            uList.add(uIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));

            List<IdxPref> iList = iidxList.get(iidx);
            if (iList == null) {
                iList = new ArrayList<>();
                iidxList.set(iidx, iList);
            }
            iList.add(iIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));
        });

        return new SimpleFastUpdateablePreferenceData<>(numPreferences.intValue(), uidxList, iidxList, uIndex, iIndex, uIdPrefFun, iIdPrefFun);
    }
}
