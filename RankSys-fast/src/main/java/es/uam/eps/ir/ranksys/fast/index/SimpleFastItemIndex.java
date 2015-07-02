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
package es.uam.eps.ir.ranksys.fast.index;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Simple implementation of FastItemIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 */
public class SimpleFastItemIndex<I> implements FastItemIndex<I>, Serializable {

    private final IdxIndex<I> iMap;

    /**
     * Constructor.
     *
     */
    protected SimpleFastItemIndex() {
        this.iMap = new IdxIndex<>();
    }

    @Override
    public boolean containsItem(I i) {
        return iMap.containsId(i);
    }

    @Override
    public int numItems() {
        return iMap.size();
    }

    @Override
    public Stream<I> getAllItems() {
        return iMap.getIds();
    }

    @Override
    public int item2iidx(I i) {
        return iMap.get(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iMap.get(iidx);
    }

    /**
     * Add a new item to the index. If the item already exists, nothing is done.
     *
     * @param i id of the item
     * @return index of the item
     */
    protected int add(I i) {
        return iMap.add(i);
    }

    /**
     * Creates a item index from a file where the first column lists the items.
     *
     * @param <I> type of the items
     * @param path path of the file
     * @param iParser item type parser
     * @return a fast item index
     * @throws IOException when file does not exist or when IO error
     */
    public static <I> SimpleFastItemIndex<I> load(String path, Parser<I> iParser) throws IOException {
        return load(path, iParser, true);
    }

    public static <I> SimpleFastItemIndex<I> load(String path, Parser<I> iParser, boolean sort) throws IOException {
        return load(new FileInputStream(path), iParser, sort);
    }

    /**
     * Creates a item index from an input stream where the first column lists the item.
     *
     * @param <I> type of the items
     * @param in input stream
     * @param iParser item type parser
     * @return a fast item index
     * @throws IOException when IO error
     */
    public static <I> SimpleFastItemIndex<I> load(InputStream in, Parser<I> iParser) throws IOException {
        return load(in, iParser, true);
    }

    public static <I> SimpleFastItemIndex<I> load(InputStream in, Parser<I> iParser, boolean sort) throws IOException {
        SimpleFastItemIndex<I> itemIndex = new SimpleFastItemIndex<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<I> items = reader.lines()
                    .map(line -> iParser.parse(split(line, '\t')[0]));
            (sort ? items.sorted() : items).forEach(i -> itemIndex.add(i));
        }
        return itemIndex;
    }

}
