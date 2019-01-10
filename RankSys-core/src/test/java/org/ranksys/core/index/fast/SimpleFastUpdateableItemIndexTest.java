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
import org.ranksys.core.index.fast.updateable.FastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.SimpleFastUpdateableItemIndex;

/**
 * Unit test for SimpleUpdateableFastItemIndex
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFastUpdateableItemIndexTest {

    /**
     * Tests main functionalities of the class.
     */
    @Test
    public void test() {
        Random rnd = new Random();
        
        int N = rnd.nextInt(5000);
        List<String> items = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(items, rnd);

        FastUpdateableItemIndex<String> itemIndex = SimpleFastUpdateableItemIndex.load(items.stream());

        assertEquals(itemIndex.numItems(), N);

        rnd.ints(1000, 0, N).forEach(iidx -> {
            assertTrue(itemIndex.iidx2item(iidx).equals(items.get(iidx)));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(item -> {
            assertTrue(itemIndex.item2iidx(item) == items.indexOf(item));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(item -> {
            assertTrue(itemIndex.containsItem(item));
        });

        rnd.ints(1000, N, 2 * N).mapToObj(Integer::toString).forEach(item -> {
            assertFalse(itemIndex.containsItem(item));
        });

        assertEquals(itemIndex.getAllItems().collect(toSet()), new HashSet<>(items));
    }
    
    @Test
    public void testAdditionsAndRemovals() {
        Random rnd = new Random();
        
        int N = rnd.nextInt(5000);
        List<String> items = IntStream.range(0,N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(items, rnd);
        
        FastUpdateableItemIndex<String> itemIndex = SimpleFastUpdateableItemIndex.load(Stream.empty());
        assertEquals(itemIndex.numItems(),0);
        
        IntStream.range(0, N).forEach(iidx -> 
        {
           assertEquals(itemIndex.addItem(items.get(iidx)), iidx); 
        });
        
        rnd.ints(1000, 0, N).forEach(iidx -> {
            assertTrue(itemIndex.iidx2item(iidx).equals(items.get(iidx)));
        });
        
        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(item -> {
            assertTrue(itemIndex.item2iidx(item) == items.indexOf(item));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(item -> {
            assertTrue(itemIndex.containsItem(item));
        });

        rnd.ints(1000, N, 2 * N).mapToObj(Integer::toString).forEach(item -> {
            assertFalse(itemIndex.containsItem(item));
        });
        
        // Remove the first N/2 elements.
        /*List<String> itemsAux = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        IntStream.range(0, N/2).forEach(idx -> 
        {
            int iidx = itemIndex.item2iidx(itemsAux.get(idx));
            assertEquals(iidx, itemIndex.removeItem(itemsAux.get(idx)));
            assertEquals(N-idx-1, itemIndex.numItems());
            items.remove(iidx);
        });
        
        // Check if positions 0 to N/2 are ocuppied by the same elements in the index and the list
        rnd.ints(1000, 0, N/2).forEach(iidx -> {
            assertTrue(itemIndex.iidx2item(iidx).equals(items.get(iidx)));
        });
        
        // Check if the remaining N/2 items have the same position in the items string than in the index.
        rnd.ints(1000, N/2+1, N).mapToObj(Integer::toString).forEach(item -> {
            assertTrue(itemIndex.item2iidx(item) == items.indexOf(item));
        });

        // Check that the index contains the elements from N/2+1 to N
        rnd.ints(1000, N/2+1, N).mapToObj(Integer::toString).forEach(item -> {
            assertTrue(itemIndex.containsItem(item));
        });

        // Check that the index does not contain the elements from 0 to N/2
        rnd.ints(1000, 0, N/2).mapToObj(Integer::toString).forEach(item -> {
            assertFalse(itemIndex.containsItem(item));
        });
        
        // Check that the index does not contain the elements from N to 2N
        rnd.ints(1000, N, 2*N).mapToObj(Integer::toString).forEach(item -> {
            assertFalse(itemIndex.containsItem(item));
        });*/
    }

}
