/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index.fast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.ranksys.core.index.fast.updateable.FastUpdateableUserIndex;
import org.ranksys.core.index.fast.updateable.SimpleFastUpdateableUserIndex;

/**
 * Unit test for SimpleUpdateableFastUserIndex
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFastUpdateableUserIndexTest {

    /**
     * Tests main functionalities of the class.
     */
    @Test
    public void test() {
        Random rnd = new Random();
        
        int N = rnd.nextInt(5000);
        List<String> users = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(users, rnd);

        FastUpdateableUserIndex<String> userIndex = SimpleFastUpdateableUserIndex.load(users.stream());

        assertEquals(userIndex.numUsers(), N);

        rnd.ints(1000, 0, N).forEach(uidx -> {
            assertTrue(userIndex.uidx2user(uidx).equals(users.get(uidx)));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(user -> {
            assertTrue(userIndex.user2uidx(user) == users.indexOf(user));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(user -> {
            assertTrue(userIndex.containsUser(user));
        });

        rnd.ints(1000, N, 2 * N).mapToObj(Integer::toString).forEach(user -> {
            assertFalse(userIndex.containsUser(user));
        });

        assertEquals(userIndex.getAllUsers().collect(toSet()), new HashSet<>(users));
    }
    
    @Test
    public void testAdditionsAndRemovals() {
        Random rnd = new Random();
        
        int N = rnd.nextInt(5000);
        List<String> users = IntStream.range(0,N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(users, rnd);
        
        FastUpdateableUserIndex<String> userIndex = SimpleFastUpdateableUserIndex.load(Stream.empty());
        assertEquals(userIndex.numUsers(),0);
        
        IntStream.range(0, N).forEach(uidx -> 
        {
           assertEquals(userIndex.addUser(users.get(uidx)), uidx); 
        });
        
        rnd.ints(1000, 0, N).forEach(uidx -> {
            assertTrue(userIndex.uidx2user(uidx).equals(users.get(uidx)));
        });
        
        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(user -> {
            assertTrue(userIndex.user2uidx(user) == users.indexOf(user));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(user -> {
            assertTrue(userIndex.containsUser(user));
        });

        rnd.ints(1000, N, 2 * N).mapToObj(Integer::toString).forEach(user -> {
            assertFalse(userIndex.containsUser(user));
        });
        
        // Remove the first N/2 elements.
        /*List<String> usersAux = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        IntStream.range(0, N/2).forEach(idx -> 
        {
            int uidx = userIndex.user2uidx(usersAux.get(idx));
            assertEquals(uidx, userIndex.removeUser(usersAux.get(idx)));
            assertEquals(N-idx-1, userIndex.numUsers());
            users.remove(uidx);
        });
        
        // Check if positions 0 to N/2 are ocuppied by the same elements in the index and the list
        rnd.ints(1000, 0, N/2).forEach(uidx -> {
            assertTrue(userIndex.uidx2user(uidx).equals(users.get(uidx)));
        });
        
        // Check if the remaining N/2 users have the same position in the users string than in the index.
        rnd.ints(1000, N/2+1, N).mapToObj(Integer::toString).forEach(user -> {
            assertTrue(userIndex.user2uidx(user) == users.indexOf(user));
        });

        // Check that the index contains the elements from N/2+1 to N
        rnd.ints(1000, N/2+1, N).mapToObj(Integer::toString).forEach(user -> {
            assertTrue(userIndex.containsUser(user));
        });

        // Check that the index does not contain the elements from 0 to N/2
        rnd.ints(1000, 0, N/2).mapToObj(Integer::toString).forEach(user -> {
            assertFalse(userIndex.containsUser(user));
        });
        
        // Check that the index does not contain the elements from N to 2N
        rnd.ints(1000, N, 2*N).mapToObj(Integer::toString).forEach(user -> {
            assertFalse(userIndex.containsUser(user));
        });*/
    }

}
