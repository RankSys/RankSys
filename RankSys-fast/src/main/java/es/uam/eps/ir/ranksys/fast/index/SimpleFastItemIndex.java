/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
     * This method sorts the item ids and then assigns integer ids in that order.
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

    /**
     * Creates a item index from a file where the first column lists the items.
     *
     * @param <I> type of the items
     * @param path path of the file
     * @param iParser item type parser
     * @param sort if true, item ids in the file are sorted before assigning integer indices
     * @return a fast item index
     * @throws IOException when file does not exist or when IO error
     */
    public static <I> SimpleFastItemIndex<I> load(String path, Parser<I> iParser, boolean sort) throws IOException {
        return load(new FileInputStream(path), iParser, sort);
    }

    /**
     * Creates a item index from an input stream where the first column lists the item.
     * This method sorts the item ids and then assigns integer ids in that order.
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

    /**
     * Creates a item index from an input stream where the first column lists the item.
     *
     * @param <I> type of the items
     * @param in input stream
     * @param iParser item type parser
     * @param sort if true, item ids in the stream are sorted before assigning integer indices
     * @return a fast item index
     * @throws IOException when IO error
     */
    public static <I> SimpleFastItemIndex<I> load(InputStream in, Parser<I> iParser, boolean sort) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<I> items = reader.lines()
                    .map(line -> iParser.parse(split(line, '\t')[0]));
            return load(sort ? items.sorted() : items);
        }
    }
    
    /**
     * Creates an item index from a stream of item objects.
     *
     * @param <I> type of the items
     * @param items stream of item objects
     * @return a fast item index
     */
    public static <I> SimpleFastItemIndex<I> load(Stream<I> items) {
        SimpleFastItemIndex<I> itemIndex = new SimpleFastItemIndex<>();
        items.forEach(itemIndex::add);
        return itemIndex;
    }

}
