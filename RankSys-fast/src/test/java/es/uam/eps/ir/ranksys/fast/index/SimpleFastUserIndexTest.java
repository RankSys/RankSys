/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.index;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.IntStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit test for SimpleFastUserIndex
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFastUserIndexTest {

    @Test
    public void test() {
        Random rnd = new Random();
        
        int N = rnd.nextInt(5000);
        List<String> users = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(users, rnd);

        FastUserIndex<String> userIndex = SimpleFastUserIndex.load(users.stream());

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

}
