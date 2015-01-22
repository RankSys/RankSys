/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.ip;
import gnu.trove.impl.Constants;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class IdxIndex<T> {

    private final TObjectIntMap<T> t2imap;
    private final TIntObjectMap<T> i2tmap;
    private int count;

    public IdxIndex() {
        t2imap = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);
        i2tmap = new TIntObjectHashMap<>();
        count = -1;
    }

    private IdxIndex(TObjectIntMap<T> t2imap, TIntObjectMap<T> i2tmap, int count) {
        this.t2imap = t2imap;
        this.i2tmap = i2tmap;
        this.count = count;
    }
    
    public IdxIndex(IdxIndex<T> mapper) {
        t2imap = new TObjectIntHashMap<>(mapper.t2imap);
        i2tmap = new TIntObjectHashMap<>(mapper.i2tmap);
        count = mapper.count;
    }

    public int add(T t) {
        int idx = t2imap.get(t);
        if (idx == -1) {
            count++;
            idx = count;
            t2imap.put(t, idx);
            i2tmap.put(idx, t);
            return idx;
        } else {
            return idx;
        }
    }
    
    public void remove(T t) {
        int idx = t2imap.get(t);
        if (idx != -1) {
            t2imap.remove(t);
            i2tmap.remove(idx);
        }
    }
    
    public int get(T t) {
        return t2imap.get(t);
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

    public void save(OutputStream stream) throws IOException {
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream));
        t2imap.forEachEntry((T a, int b) -> {
            try {
                out.write(a.toString());
                out.write('\t');
                out.write(Integer.toString(b));
                out.newLine();
            } catch (IOException ex) {
                getLogger(IdxIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        });
        out.flush();
    }

    public static <T> IdxIndex<T> load(InputStream stream, Parser<T> tParser) throws IOException {
        TObjectIntMap<T> t2imap = new TObjectIntHashMap<>();
        TIntObjectMap<T> i2tmap = new TIntObjectHashMap<>();
        int count = -1;

        final BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = in.readLine()) != null) {
            CharSequence[] tokens = split(line, '\t');
            T a = tParser.parse(tokens[0]);
            int b = ip.parse(tokens[1]);

            t2imap.put(a, b);
            i2tmap.put(b, a);
            count++;
        }

        return new IdxIndex<>(t2imap, i2tmap, count);
    }
}
