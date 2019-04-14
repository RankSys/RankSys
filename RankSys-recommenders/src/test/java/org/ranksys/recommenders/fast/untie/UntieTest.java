/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.recommenders.fast.untie;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.jooq.lambda.tuple.Tuple3;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.ranksys.core.Recommendation;
import org.ranksys.core.index.fast.updateable.FastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.FastUpdateableUserIndex;
import org.ranksys.core.index.fast.updateable.SimpleFastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.SimpleFastUpdateableUserIndex;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.preference.fast.SimpleFastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2od;
import org.ranksys.core.util.unties.fast.RandomUntiePolicy;
import org.ranksys.core.util.unties.UntiePolicy;
import org.ranksys.recommenders.fast.FastRankingRecommender;

/**
 * Class for testing the untie mechanisms in recommendations.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 */
public class UntieTest 
{
    @Test
    public void test()
    {
        Random rnd = new Random();
        // Number of users
        int N = rnd.nextInt(5000);
        // Number of items
        int M = rnd.nextInt(2000);
                
        List<String> users = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(users);
        List<String> items = IntStream.range(0, M).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(items);
        
        FastUpdateableUserIndex<String> uIndex = SimpleFastUpdateableUserIndex.load(users.stream());
        FastUpdateableItemIndex<String> iIndex = SimpleFastUpdateableItemIndex.load(items.stream());
        
        List<Tuple3<String,String,Double>> prefs = new ArrayList<>();
        int numPref = users.stream().mapToInt(u -> 
        {
            int K = rnd.nextInt(Math.min(500, M));
            Set<Integer> set = new HashSet<>();
            rnd.ints(K,0,M).forEach(k -> 
            {
                int aux = k;
                while(set.contains(aux))
                {
                    aux = (aux+1)%M;
                }
                set.add(aux);
                prefs.add(new Tuple3<>(u, items.get(aux),5*rnd.nextDouble()));
            });
            
            return K;
        }).sum();
        
        FastPreferenceData<String,String> prefData = SimpleFastPreferenceData.load(prefs.stream(), uIndex, iIndex);
        
        FastRankingRecommender<String, String> fastRankingRec = new AuxRecommender<>(prefData);
        for(String u : users)
        {
            Recommendation<String, String> rec = fastRankingRec.getRecommendation(u,10);
            List<Tuple2od<String>> list = rec.getItems();
            for(int i = 0; i < 10; ++i)
            {
                assertEquals(items.get(items.size()-i-1), list.get(i).v1);
            }
        }       
    }
    
    private class AuxRecommender<U,I> extends FastRankingRecommender<U,I>
    {
      
        public AuxRecommender(FastPreferenceData<U,I> prefData)
        {
            super(prefData, prefData);
        }
        
        @Override
        public Int2DoubleMap getScoresMap(int uidx)
        {
           Int2DoubleMap map = new Int2DoubleOpenHashMap();
           this.iIndex.getAllIidx().forEach(iidx -> map.put(iidx, 1.0));
           return map;
        }
    }
}
