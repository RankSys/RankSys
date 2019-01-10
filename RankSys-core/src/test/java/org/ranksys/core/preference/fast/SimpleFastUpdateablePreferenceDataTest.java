/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.core.preference.fast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.ranksys.core.index.fast.updateable.FastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.FastUpdateableUserIndex;
import org.ranksys.core.index.fast.updateable.SimpleFastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.SimpleFastUpdateableUserIndex;
import org.ranksys.core.preference.IdPref;
import org.ranksys.core.preferences.fast.updateable.SimpleFastUpdateablePreferenceData;

/**
 * Class for testing the simple fast updateable preference data.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 */
public class SimpleFastUpdateablePreferenceDataTest 
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
        
        SimpleFastUpdateablePreferenceData<String,String> prefData = SimpleFastUpdateablePreferenceData.load(prefs.stream(), uIndex, iIndex);
        assertEquals(prefData.numUsers(), N);
        assertEquals(prefData.numItems(), M);
        assertEquals(prefData.numPreferences(), numPref);
        
        rnd.ints(1000, 0, numPref).forEach(i -> 
        {
            String user = prefs.get(i).v1;
            String item = prefs.get(i).v2;
            double val = prefs.get(i).v3;
            
            Optional<IdxPref> optional = prefData.getPreference(uIndex.user2uidx(user), iIndex.item2iidx(item));
            if(optional.isPresent())
            {
                assertEquals(val, optional.get().v2,0.00001);
            }
            else
            {
                assertFalse(true);
            }
            
        });
        
        List<Tuple3<String,String,Double>> extraPrefs = new ArrayList<>();
        // Add some preferences.
        int numExtraPref = users.stream().mapToInt(u -> 
        {
            int K = rnd.nextInt(Math.min(100, M - prefData.numItems(u)));
            Set<Integer> set = prefData.getUidxPreferences(prefData.user2uidx(u)).map(pref -> pref.v1).collect(Collectors.toCollection(HashSet::new));
            rnd.ints(K,0,M).forEach(k -> 
            {
                int aux = k;
                while(set.contains(aux))
                {
                    aux = (aux+1)%M;
                }
                set.add(aux);
                extraPrefs.add(new Tuple3<>(u, items.get(aux),5*rnd.nextDouble()));
            });
            
            return K;
        }).sum();
        
        prefData.update(extraPrefs.stream());

        assertEquals(prefData.numPreferences(), numPref + numExtraPref);
        rnd.ints(1000, 0, numExtraPref).forEach(i -> 
        {
            String user = extraPrefs.get(i).v1;
            String item = extraPrefs.get(i).v2;
            double val = extraPrefs.get(i).v3;

            Optional<IdxPref> optional = prefData.getPreference(uIndex.user2uidx(user), iIndex.item2iidx(item));
            if(optional.isPresent())
            {
                assertEquals(val, optional.get().v2,0.00001);
            }
            else
            {
                assertFalse(true);
            }

        });
               
        
        List<Tuple3<String,String,Double>> falsePrefs = new ArrayList<>();
        // Generate false preferences (not included items).
        int falsePref = users.stream().mapToInt(u -> 
        {
            int K = rnd.nextInt(Math.min(50, M));
            Set<Integer> set = new HashSet<>();
            rnd.ints(K,0,200).forEach(k -> 
            {
                int aux = k;
                while(set.contains(aux))
                {
                    aux = (aux+1)%200;
                }
                set.add(aux);
                falsePrefs.add(new Tuple3<>(u, Integer.toString(aux+M),5*rnd.nextDouble()));
            });
            
            return K;
        }).sum();
        
        // Generate false preferences (not included users).
        falsePref += IntStream.range(N,N+200).map(u -> 
        {
            int K = rnd.nextInt(Math.min(50, M));
            Set<Integer> set = new HashSet<>();
            rnd.ints(K,0,M).forEach(k -> 
            {
                int aux = k;
                while(set.contains(aux))
                {
                    aux = (aux+1)%M;
                }
                set.add(aux);
                falsePrefs.add(new Tuple3<>(Integer.toString(u), Integer.toString(aux),5*rnd.nextDouble()));
            });
            
            return K;
        }).sum();
        
        prefData.update(falsePrefs.stream());
        assertEquals(prefData.numUsers(), N);
        assertEquals(prefData.numItems(), M);
        assertEquals(prefData.numPreferences(), numPref + numExtraPref);
        assertEquals(uIndex.numUsers(), N);
        assertEquals(iIndex.numItems(), M);
        
        List<String> extraUsers = IntStream.range(N, N+200).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(extraUsers);
        List<String> extraItems = IntStream.range(M, M+200).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(extraItems);
        
        extraUsers.forEach(u -> prefData.updateAddUser(u));
        extraItems.forEach(i -> prefData.updateAddItem(i));
        assertEquals(prefData.numUsers(), N+200);
        assertEquals(prefData.numItems(), M+200);
        
        falsePrefs.forEach(t -> prefData.update(t.v1,t.v2,t.v3));
        prefData.update(falsePrefs.stream());

        assertEquals(prefData.numPreferences(), numPref + numExtraPref + falsePref);
        
        
        // Delete users
        /*Collections.shuffle(extraUsers);
        extraUsers.forEach(u -> prefData.updateRemoveUser(u));
        Collections.shuffle(extraItems);
        extraItems.forEach(i -> prefData.updateRemoveItem(i));
        
        assertEquals(prefData.numUsers(), N);
        assertEquals(prefData.numItems(), M);
        assertEquals(prefData.numPreferences(), numPref + numExtraPref);*/
        
        List<Tuple2<String,String>> prefsToDelete = new ArrayList<>();
        
        // Delete some ratings
        int numDeleted = users.stream().mapToInt(u -> 
        {
            int K = rnd.nextInt(Math.min(20, prefData.numItems(u)));
            List<IdPref<String>> ls = new ArrayList<>();
            prefData.getUserPreferences(u).forEach(pref -> ls.add(pref));
            Collections.shuffle(ls);
            
            ls.subList(0, K).forEach(pref -> prefsToDelete.add(new Tuple2<>(u, pref.v1)));
            return K;
        }).sum();

        assertEquals(numDeleted, prefsToDelete.size());
        prefData.updateDelete(prefsToDelete.stream());
        assertEquals(prefData.numUsers(), N+200);
        assertEquals(prefData.numItems(), M+200);
        assertEquals(prefData.numPreferences(), numPref+numExtraPref+falsePref-numDeleted);
        rnd.ints(1000, 0, numDeleted).forEach(i -> 
        {
            String user = prefsToDelete.get(i).v1;
            String item = prefsToDelete.get(i).v2;
            
            Optional<IdxPref> optional = prefData.getPreference(uIndex.user2uidx(user), iIndex.item2iidx(item));
            assertFalse(optional.isPresent());
        });
    }
}
