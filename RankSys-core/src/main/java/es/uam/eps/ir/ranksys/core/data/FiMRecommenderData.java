package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.IdxValuePair;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.IdxMapper;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.TransformIterator;

/**
 *
 * @author saul
 */
public class FiMRecommenderData<U, I, V> implements LRecommenderData<U, I, V> {

    private TIntArrayList uidxl;
    private TIntArrayList iidxl;
    private List<V> vl;
    private List<TIntArrayList> uidxIndex;
    private List<TIntArrayList> iidxIndex;
    private final IdxMapper<U> uMap;
    private final IdxMapper<I> iMap;

    public FiMRecommenderData() {
        uidxl = new TIntArrayList();
        iidxl = new TIntArrayList();
        vl = new ArrayList<>();
        uMap = new IdxMapper<>();
        iMap = new IdxMapper<>();

        uidxIndex = singleIndex(uidxl, uMap.size());
        iidxIndex = singleIndex(iidxl, iMap.size());
    }

    public FiMRecommenderData(String file, Parser<U> uParser, Parser<I> iParser, Parser<V> vParser) throws IOException {
        this(new String[]{file}, uParser, iParser, vParser);
    }

    public FiMRecommenderData(String[] files, Parser<U> uParser, Parser<I> iParser, Parser<V> vParser) throws IOException {
        uidxl = new TIntArrayList();
        iidxl = new TIntArrayList();
        vl = new ArrayList<>();

        uMap = new IdxMapper<>();
        iMap = new IdxMapper<>();

        for (String file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file), 10 * 1024 * 1024)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t');
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    V v = vParser.parse(tokens[2]);

                    int uidx = uMap.add(u);
                    int iidx = iMap.add(i);

                    uidxl.add(uidx);
                    iidxl.add(iidx);
                    vl.add(v);
                }
            }
        }

        uidxIndex = singleIndex(uidxl, uMap.size());
        iidxIndex = singleIndex(iidxl, iMap.size());
    }

    protected FiMRecommenderData(TIntArrayList uidxl, TIntArrayList iidxl, List<V> vl, List<TIntArrayList> uidxIndex, List<TIntArrayList> iidxIndex, IdxMapper<U> uMap, IdxMapper<I> iMap) {
        this.uidxl = uidxl;
        this.iidxl = iidxl;
        this.vl = vl;
        this.uidxIndex = uidxIndex;
        this.iidxIndex = iidxIndex;
        this.uMap = uMap;
        this.iMap = iMap;
    }

    protected FiMRecommenderData<U, I, V> copy() {
        TIntArrayList _uidxl = new TIntArrayList(uidxl);
        TIntArrayList _iidxl = new TIntArrayList(iidxl);
        List<V> _vl = new ArrayList<>(vl);

        List<TIntArrayList> _uidxIndex = singleIndex(uidxl, uMap.size());
        List<TIntArrayList> _iidxIndex = singleIndex(iidxl, iMap.size());

        return new FiMRecommenderData<>(_uidxl, _iidxl, _vl, _uidxIndex, _iidxIndex, uMap, iMap);
    }

    private static List<TIntArrayList> singleIndex(TIntArrayList a, int n) {
        List<TIntArrayList> index = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            index.add(i, new TIntArrayList());
        }
        for (int j = 0; j < a.size(); j++) {
            index.get(a.getQuick(j)).add(j);
        }

        return index;
    }

    public void reindex(boolean byUser) {
        if (byUser) {
            reindex(uidxIndex);
        } else {
            reindex(iidxIndex);
        }
    }

    private void reindex(List<TIntArrayList> index) {
        TIntArrayList _uidxl = new TIntArrayList(uidxl.size());
        TIntArrayList _iidxl = new TIntArrayList(iidxl.size());
        List<V> _vl = new ArrayList<>(vl.size());
        for (TIntArrayList list : index) {
            for (int i = 0; i < list.size(); i++) {
                _uidxl.add(uidxl.get(list.get(i)));
                _iidxl.add(iidxl.get(list.get(i)));
                _vl.add(vl.get(list.get(i)));
            }
        }
        this.uidxl = _uidxl;
        this.iidxl = _iidxl;
        this.vl = _vl;

        uidxIndex = singleIndex(uidxl, uMap.size());
        iidxIndex = singleIndex(iidxl, iMap.size());
    }

    @Override
    public IdxMapper<U> getUserMapper() {
        return uMap;
    }

    @Override
    public IdxMapper<I> getItemMapper() {
        return iMap;
    }

    @Override
    public int user2uidx(U u) {
        return uMap.get(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uMap.get(uidx);
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
    public int numUsers() {
        return uMap.size();
    }

    @Override
    public int numUsers(int iidx) {
        if (iidx < 0 || iidx >= iidxIndex.size()) {
            return 0;
        }
        return iidxIndex.get(iidx).size();
    }

    @Override
    public int numUsers(I i) {
        return numUsers(iMap.get(i));
    }

    @Override
    public int numItems() {
        return iMap.size();
    }

    @Override
    public int numItems(int uidx) {
        if (uidx < 0 || uidx >= uidxIndex.size()) {
            return 0;
        }
        return uidxIndex.get(uidx).size();
    }

    @Override
    public int numItems(U u) {
        return numItems(uMap.get(u));
    }

    @Override
    public int numPreferences() {
        return vl.size();
    }

    @Override
    public Iterable<U> getAllUsers() {
        return uMap.getIds();
    }

    @Override
    public Iterable<I> getAllItems() {
        return iMap.getIds();
    }

    @Override
    public Iterable<IdxValuePair<V>> getUidxPreferences(final int uidx) {
        return () -> {
            if (uidx < 0 || uidx >= uidxIndex.size()) {
                return EmptyIterator.INSTANCE;
            }
            return new IdxValuePairIterator(uidxIndex.get(uidx), iidxl);
        };
    }

    @Override
    public Iterable<IdValuePair<I, V>> getUserPreferences(final U u) {
        return () -> {
            TransformIterator iterator = new TransformIterator(getUidxPreferences(uMap.get(u)).iterator(), new Transformer() {

                private final IdValuePair<I, V> pair2 = new IdValuePair<>();

                @Override
                public Object transform(Object input) {
                    IdxValuePair<V> pair1 = (IdxValuePair<V>) input;
                    pair2.id = iMap.get(pair1.idx);
                    pair2.v = pair1.v;

                    return pair2;
                }
            });

            return iterator;
        };
    }

    @Override
    public Iterable<IdxValuePair<V>> getIidxPreferences(final int iidx) {
        return () -> {
            if (iidx < 0 || iidx >= iidxIndex.size()) {
                return EmptyIterator.INSTANCE;
            }
            return new IdxValuePairIterator(iidxIndex.get(iidx), uidxl);
        };
    }

    @Override
    public Iterable<IdValuePair<U, V>> getItemPreferences(final I i) {
        return () -> {
            TransformIterator iterator = new TransformIterator(getIidxPreferences(iMap.get(i)).iterator(), new Transformer() {

                private final IdValuePair<U, V> pair2 = new IdValuePair<>();

                @Override
                public Object transform(Object input) {
                    IdxValuePair<V> pair1 = (IdxValuePair<V>) input;
                    pair2.id = uMap.get(pair1.idx);
                    pair2.v = pair1.v;

                    return pair2;
                }
            });

            return iterator;
        };
    }

    @Override
    public void addUser(U user) {
        int uidx = uMap.add(user);
        if (uidx >= uidxIndex.size()) {
            uidxIndex.add(uidx, new TIntArrayList());
        }
    }

    public void removeUserPreferences(U user) {
        int uidx = uMap.get(user);
        uidxIndex.get(uidx).clear();
    }

    @Override
    public void addItem(I item) {
        int iidx = iMap.add(item);
        if (iidx >= iidxIndex.size()) {
            iidxIndex.add(iidx, new TIntArrayList());
        }
    }

    @Override
    public void addUserPreferences(U user, Iterable<IdValuePair<I, V>> userPreferences) {
        int uidx = uMap.get(user);
        for (IdValuePair<I, V> iv : userPreferences) {
            int iidx = iMap.get(iv.id);
            uidxl.add(uidx);
            iidxl.add(iidx);
            vl.add(iv.v);
            uidxIndex.get(uidx).add(uidxl.size() - 1);
            iidxIndex.get(iidx).add(iidxl.size() - 1);
        }
    }

    @Override
    public void addItemPreferences(I item, Iterable<IdValuePair<U, V>> itemPreferences) {
        int iidx = iMap.get(item);
        for (IdValuePair<U, V> uv : itemPreferences) {
            int uidx = uMap.get(uv.id);
            uidxl.add(uidx);
            iidxl.add(iidx);
            vl.add(uv.v);
            uidxIndex.get(uidx).add(uidxl.size() - 1);
            iidxIndex.get(iidx).add(iidxl.size() - 1);
        }
    }

    @Override
    public IntStream getAllUidx() {
        return IntStream.range(0, uidxIndex.size());
    }

    @Override
    public IntStream getAllIidx() {
        return IntStream.range(0, iidxIndex.size());
    }

    private class IdxValuePairIterator implements Iterator<IdxValuePair<V>> {

        private final IdxValuePair<V> pair;
        private final TIntArrayList idxs;
        private final TIntArrayList idxl;
        private int i;

        public IdxValuePairIterator(TIntArrayList idxs, TIntArrayList idxl) {
            this.pair = new IdxValuePair<>();
            this.idxs = idxs;
            this.idxl = idxl;
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            return i < idxs.size();
        }

        @Override
        public IdxValuePair<V> next() {
            pair.idx = idxl.getQuick(idxs.getQuick(i));
            pair.v = vl.get(idxs.getQuick(i));
            i++;

            return pair;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
