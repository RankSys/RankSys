/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import com.zaxxer.hikari.HikariDataSource;
import es.uam.eps.ir.ranksys.fast.preference.SQLPreferenceData;
import org.jooq.SQLDialect;
import org.junit.Test;

import static java.lang.Double.parseDouble;
import static java.util.stream.Stream.of;
import static org.junit.Assert.assertTrue;

/**
 * A test for SQLPreferenceData.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SQLPreferenceDataTest {

    /**
     * Test with toy data.
     */
    @Test
    public void testToyExample() {
        String url = "jdbc:h2:mem:test";

        try (HikariDataSource ds = new HikariDataSource()) {
            ds.setJdbcUrl(url);
            
            SQLPreferenceData.create(ds, SQLDialect.H2, "data");
            
            SQLPreferenceData prefs = new SQLPreferenceData(ds, SQLDialect.H2, "data");
            
            String[][] data = new String[][]{
                new String[]{"a", "A", "1"},
                new String[]{"a", "B", "2"},
                new String[]{"a", "E", "3"},
                new String[]{"a", "T", "4"},
                new String[]{"a", "Q", "5"},
                new String[]{"a", "Y", "1"},
                new String[]{"a", "H", "2"},
                new String[]{"a", "M", "3"},
                new String[]{"a", "D", "4"},
                new String[]{"b", "A", "5"},
                new String[]{"b", "B", "1"},
                new String[]{"b", "C", "2"},
                new String[]{"b", "D", "3"},
                new String[]{"b", "E", "4"},
                new String[]{"c", "A", "5"},
                new String[]{"c", "C", "1"},
                new String[]{"c", "T", "2"},
                new String[]{"c", "Y", "3"},
                new String[]{"c", "U", "4"},
                new String[]{"c", "I", "5"},
                new String[]{"c", "O", "1"},
            };
            
            int numPreferences = data.length;
            int numUsers = (int) of(data).map(p -> p[0]).distinct().count();
            int numItems = (int) of(data).map(p -> p[1]).distinct().count();

            for (String[] pref : data) {
                if (!prefs.containsUser(pref[0])) {
                    assertTrue(prefs.addUser(pref[0]));
                }
                if (!prefs.containsItem(pref[1])) {
                    assertTrue(prefs.addItem(pref[1]));
                }
                assertTrue(prefs.addPref(pref[0], pref[1], parseDouble(pref[2]), null));
            }
            
            assertTrue(prefs.numPreferences() == numPreferences);
            assertTrue(prefs.numUsers() == numUsers);
            assertTrue(prefs.numItems() == numItems);
            assertTrue(prefs.numItems("a") == 9);
            assertTrue(prefs.numItems("b") == 5);
            assertTrue(prefs.numItems("c") == 7);
            assertTrue(prefs.numUsers("A") == 3);
            assertTrue(prefs.numUsers("B") == 2);
            
            assertTrue(prefs.removeUser("a"));
            assertTrue(prefs.numPreferences() == numPreferences - 9);
        }

    }
}
