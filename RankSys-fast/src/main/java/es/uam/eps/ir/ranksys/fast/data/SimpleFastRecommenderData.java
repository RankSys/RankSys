/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.data;

import es.uam.eps.ir.ranksys.core.IdPref;
import es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.fast.IdxPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public class SimpleFastRecommenderData<U, I, O> implements FastRecommenderData<U, I, O> {

    private final int numPreferences;
    private final TIntObjectMap<List<IdxPref<O>>> uidxMap;
    private final TIntObjectMap<List<IdxPref<O>>> iidxMap;
    private final FastUserIndex<U> uMap;
    private final FastItemIndex<I> iMap;

    public SimpleFastRecommenderData(int numPreferences, TIntObjectMap<List<IdxPref<O>>> uidxMap, TIntObjectMap<List<IdxPref<O>>> iidxMap, FastUserIndex<U> uMap, FastItemIndex<I> iMap) {
        this.numPreferences = numPreferences;
        this.uidxMap = uidxMap;
        this.iidxMap = iidxMap;
        this.uMap = uMap;
        this.iMap = iMap;
    }

    @Override
    public int numUsers(int iidx) {
        return iidxMap.get(iidx).size();
    }

    @Override
    public int numItems(int uidx) {
        return uidxMap.get(uidx).size();
    }

    @Override
    public IntStream getAllUidx() {
        return IntStream.range(0, uidxMap.size());
    }

    @Override
    public IntStream getAllIidx() {
        return IntStream.range(0, iidxMap.size());
    }

    @Override
    public Stream<IdxPref<O>> getUidxPreferences(int uidx) {
        return uidxMap.get(uidx).stream();
    }

    @Override
    public Stream<IdxPref<O>> getIidxPreferences(int iidx) {
        return iidxMap.get(iidx).stream();
    }

    @Override
    public int numUsers(I i) {
        return numUsers(item2iidx(i));
    }

    @Override
    public int numItems(U u) {
        return numItems(user2uidx(u));
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public Stream<IdPref<I, O>> getUserPreferences(U u) {
        return getUidxPreferences(user2uidx(u)).map(iv -> new IdPref<>(iidx2item(iv.idx), iv.v, iv.o));
    }

    @Override
    public Stream<IdPref<U, O>> getItemPreferences(I i) {
        return getIidxPreferences(item2iidx(i)).map(uv -> new IdPref<>(uidx2user(uv.idx), uv.v, uv.o));
    }

    @Override
    public boolean containsUser(U u) {
        return uMap.containsUser(u);
    }

    @Override
    public int numUsers() {
        return uMap.numUsers();
    }

    @Override
    public Stream<U> getAllUsers() {
        return uMap.getAllUsers();
    }

    @Override
    public boolean containsItem(I i) {
        return iMap.containsItem(i);
    }

    @Override
    public int numItems() {
        return iMap.numItems();
    }

    @Override
    public Stream<I> getAllItems() {
        return iMap.getAllItems();
    }

    @Override
    public int user2uidx(U u) {
        return uMap.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uMap.uidx2user(uidx);
    }

    @Override
    public int item2iidx(I i) {
        return iMap.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iMap.iidx2item(iidx);
    }

    public static <U, I, V> SimpleFastRecommenderData<U, I, V> load(String path, Parser<U> uParser, Parser<I> iParser, DoubleParser dp, Parser<V> vParser) throws IOException {
        return load(new FileInputStream(path), uParser, iParser, dp, vParser);
    }

    public static <U, I, O> SimpleFastRecommenderData<U, I, O> load(InputStream in, Parser<U> uParser, Parser<I> iParser, DoubleParser dp, Parser<O> vParser) throws IOException {
        int[] numPreferences = new int[]{0};
        TIntObjectMap<List<IdxPref<O>>> uidxMap = new TIntObjectHashMap<>();
        TIntObjectMap<List<IdxPref<O>>> iidxMap = new TIntObjectHashMap<>();
        SimpleFastUserIndex<U> uMap = new SimpleFastUserIndex<>();
        SimpleFastItemIndex<I> iMap = new SimpleFastItemIndex<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                String[] tokens = l.split("\t", 4);
                U user = uParser.parse(tokens[0]);
                I item = iParser.parse(tokens[1]);
                double value;
                if (tokens.length >= 3) {
                    value = dp.parse(tokens[2]);
                } else {
                    value = dp.parse(null);
                }
                O other;
                if (tokens.length == 4) {
                    other = vParser.parse(tokens[3]);
                } else {
                    other = vParser.parse(null);
                }

                int uidx = uMap.add(user);
                int iidx = iMap.add(item);

                numPreferences[0]++;

                List<IdxPref<O>> uList = uidxMap.get(uidx);
                if (uList == null) {
                    uList = new ArrayList<>();
                    uidxMap.put(uidx, uList);
                }
                uList.add(new IdxPref<>(iidx, value, other));

                List<IdxPref<O>> iList = iidxMap.get(iidx);
                if (iList == null) {
                    iList = new ArrayList<>();
                    iidxMap.put(iidx, iList);
                }
                iList.add(new IdxPref<>(uidx, value, other));
            });
        }

        return new SimpleFastRecommenderData<>(numPreferences[0], uidxMap, iidxMap, uMap, iMap);
    }

}
