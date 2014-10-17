package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.IdxValuePair;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.IdxMapper;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class FiMFeatureData<I, F, V> implements LFeatureData<I, F, V> {

    private final int[] iidxa;
    private final int[] fidxa;
    private final V[] va;
    private final int[][] iidxIndex;
    private final int[][] fidxIndex;
    private final IdxMapper<I> iMap;
    private final IdxMapper<F> fMap;

    public FiMFeatureData(String file, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser, IdxMapper<I> iMap) throws IOException {
        this(new String[]{file}, iParser, fParser, vParser, iMap);
    }

    public FiMFeatureData(String[] files, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser, IdxMapper<I> iMap) throws IOException {
        TIntList iidxl = new TIntArrayList();
        TIntList fidxl = new TIntArrayList();
        ArrayList<V> vl = new ArrayList<>();

        if (iMap == null) {
            this.iMap = new IdxMapper<>();
        } else {
            this.iMap = iMap;
        }
        fMap = new IdxMapper<>();

        for (String file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t');
                    I i = iParser.parse(tokens[0]);
                    F f = fParser.parse(tokens[1]);
                    V v;
                    if (tokens.length == 2) {
                        v = vParser.parse(null);
                    } else {
                        v = vParser.parse(tokens[2]);
                    }

                    int iidx = this.iMap.add(i);
                    int fidx = fMap.add(f);

                    iidxl.add(iidx);
                    fidxl.add(fidx);
                    vl.add(v);
                }
            }
        }

        iidxa = iidxl.toArray();
        fidxa = fidxl.toArray();
        va = (V[]) vl.toArray();

        iidxIndex = singleIndex(iidxa, this.iMap.size());
        fidxIndex = singleIndex(fidxa, fMap.size());
    }

    private static int[][] singleIndex(int[] a, int n) {
        int[][] index = new int[n][];
        TIntList[] temp = new TIntList[n];

        for (int i = 0; i < temp.length; i++) {
            temp[i] = new TIntArrayList();
        }
        for (int i = 0; i < a.length; i++) {
            temp[a[i]].add(i);
        }
        for (int i = 0; i < temp.length; i++) {
            index[i] = temp[i].toArray();
        }

        return index;
    }

    @Override
    public IdxMapper<I> getItemMapper() {
        return iMap;
    }

    @Override
    public IdxMapper<F> getFeatureMapper() {
        return fMap;
    }

    @Override
    public int item2iidx(I i) {
        return iMap.get(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iMap.get(iidx);
    }

    @Override
    public int feature2fidx(F f) {
        return fMap.get(f);
    }

    @Override
    public F fidx2feature(int fidx) {
        return fMap.get(fidx);
    }

    @Override
    public int numItems() {
        return iMap.size();
    }

    @Override
    public int numItems(int fidx) {
        return fidxIndex[fidx].length;
    }

    @Override
    public int numItems(F f) {
        return numItems(fMap.get(f));
    }

    @Override
    public int numFeatures() {
        return fMap.size();
    }

    @Override
    public int numFeatures(int iidx) {
        if (iidx < 0 || iidx >= iidxIndex.length) {
            return 0;
        } else {
            return iidxIndex[iidx].length;
        }
    }

    @Override
    public int numFeatures(I i) {
        return numFeatures(iMap.get(i));
    }

    @Override
    public Stream<F> getAllFeatures() {
        return StreamSupport.stream(fMap.getIds().spliterator(), false);
    }

    @Override
    public Stream<I> getAllItems() {
        return StreamSupport.stream(iMap.getIds().spliterator(), false);
    }

    @Override
    public Stream<IdxValuePair<V>> getFidxItems(final int fidx) {
        if (fidx < 0 || fidx >= fidxIndex.length) {
            return Stream.empty();
        } else {
            return Arrays.stream(fidxIndex[fidx]).mapToObj(idx -> new IdxValuePair<V>(iidxa[idx], va[idx]));
        }
    }

    @Override
    public Stream<IdValuePair<I, V>> getFeatureItems(final F f) {
        return getFidxItems(fMap.get(f)).map(iv -> new IdValuePair<I, V>(iMap.get(iv.idx), iv.v));
    }

    @Override
    public Stream<IdxValuePair<V>> getIidxFeatures(final int iidx) {
        if (iidx < 0 || iidx >= iidxIndex.length) {
            return Stream.empty();
        } else {
            return Arrays.stream(iidxIndex[iidx]).mapToObj(idx -> new IdxValuePair<V>(fidxa[idx], va[idx]));
        }
    }

    @Override
    public Stream<IdValuePair<F, V>> getItemFeatures(final I i) {
        return getIidxFeatures(iMap.get(i)).map(fv -> new IdValuePair<F, V>(fMap.get(fv.idx), fv.v));
    }
}
