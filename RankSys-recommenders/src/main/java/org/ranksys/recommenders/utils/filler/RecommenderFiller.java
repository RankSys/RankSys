/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.utils.filler;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.Recommendation;
import org.ranksys.core.fast.FastRecommendation;
import org.ranksys.core.preference.fast.FastPreferenceData;

/**
 * Filler that completes the ranking using the results for another algorithm.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class RecommenderFiller<U,I> extends AbstractFastFiller<U,I>
{
    /**
     * Map for the filler rankings
     */
    private final Map<U, List<I>> recs;
    /**
     * Map for the filler rankings (fast version)
     */
    private final Int2ObjectMap<IntList> fastrecs;
    
    /**
     * Constructor.
     * @param prefData preference data.
     * @param recs map containing the recommendations, using the user identifier as key.
     */
    public RecommenderFiller(FastPreferenceData<U, I> prefData, Map<U, Recommendation<U,I>> recs)
    {
        super(prefData);
        this.recs = new HashMap<>();
        this.fastrecs = new Int2ObjectOpenHashMap<>();
        recs.forEach((user,rec) -> 
        {
            int uidx = this.prefData.user2uidx(user);
            
            List<I> list = new ArrayList<>();
            IntList intlist = new IntArrayList();
            
            rec.getItems().forEach(i ->
            {
                list.add(i.v1);
                intlist.add(this.prefData.item2iidx(i.v1));
            });
            
            this.recs.put(user, list);
            this.fastrecs.put(uidx, intlist);
        });
    }
    
    /**
     * Constructor.
     * @param prefData preference data.
     * @param fastRecs map containing the recommendations, using the user (integer) identifier as key.
     */
    public RecommenderFiller(FastPreferenceData<U, I> prefData, Int2ObjectMap<FastRecommendation> fastRecs)
    {
        super(prefData);
        this.recs = new HashMap<>();
        this.fastrecs = new Int2ObjectOpenHashMap<>();
        fastRecs.forEach((uidx,rec) -> 
        {
            U user = this.prefData.uidx2user(uidx);
            
            List<I> list = new ArrayList<>();
            IntList intlist = new IntArrayList();
            
            rec.getIidxs().forEach(i ->
            {
                intlist.add(i.v1);
                list.add(this.prefData.iidx2item(i.v1));
            });
            
            this.recs.put(user, list);
            this.fastrecs.put((int) uidx, intlist);
        });
    }
    
    @Override
    protected void resetMethod()
    {
        
    }

    @Override
    public IntStream fillerList(int uidx)
    {
        return this.fastrecs.get(uidx).intStream();
        
    }

    @Override
    public Stream<I> fillerList(U u)
    {
        return this.recs.get(u).stream();
    }

}
