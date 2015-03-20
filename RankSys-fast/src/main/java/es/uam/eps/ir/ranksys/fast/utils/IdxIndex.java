/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.fast.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class IdxIndex<T> {

    private final Object2IntMap<T> t2imap;
    private final List<T> i2tmap;

    public IdxIndex() {
        t2imap = new Object2IntOpenHashMap<>();
        t2imap.defaultReturnValue(-1);
        i2tmap = new ArrayList<>();
    }

    private IdxIndex(Object2IntMap<T> t2imap, ArrayList<T> i2tmap, int count) {
        this.t2imap = t2imap;
        this.i2tmap = i2tmap;
    }
    
    public IdxIndex(IdxIndex<T> mapper) {
        t2imap = new Object2IntOpenHashMap<>(mapper.t2imap);
        i2tmap = new ArrayList<>(mapper.i2tmap);
    }

    public int add(T t) {
        int idx = t2imap.getInt(t);
        if (idx == -1) {
            idx = i2tmap.size();
            t2imap.put(t, idx);
            i2tmap.add(t);
            return idx;
        } else {
            return idx;
        }
    }
    
    public void remove(T t) {
        int idx = t2imap.getInt(t);
        if (idx != -1) {
            t2imap.removeInt(t);
            i2tmap.remove(idx);
        }
    }
    
    public int get(T t) {
        return t2imap.getInt(t);
    }

    public T get(int idx) {
        return i2tmap.get(idx);
    }

    public boolean containsId(T t) {
        return t2imap.containsKey(t);
    }

    public int size() {
        return t2imap.size();
    }

    public Iterable<T> getIds() {
        return t2imap.keySet();
    }
}
