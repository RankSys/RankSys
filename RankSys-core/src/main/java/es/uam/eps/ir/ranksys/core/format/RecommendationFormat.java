/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.format;

import es.uam.eps.ir.ranksys.core.Recommendation;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

/**
 * Recommendation writers and readers with a common format.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface RecommendationFormat<U, I> {

    /**
     * Gets a writer for a file path.
     *
     * @param path file path
     * @return a recommendation writer
     * @throws IOException if path does not exist or IO error
     */
    public default Writer<U, I> getWriter(String path) throws IOException {
        return getWriter(new File(path));
    }
    
    /**
     * Gets a writer for a file.
     *
     * @param file file
     * @return a recommendation writer
     * @throws IOException if path does not exist or IO error
     */
    public default Writer<U, I> getWriter(File file) throws IOException {
        return getWriter(new FileOutputStream(file));
    }
    
    /**
     * Gets a writer for an output stream.
     *
     * @param out output stream
     * @return a recommendation writer
     * @throws IOException if path does not exist or IO error
     */
    public Writer<U, I> getWriter(OutputStream out) throws IOException;

    /**
     * Recommendation writer.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    public interface Writer<U, I> extends Closeable {

        /**
         * Writes the recommendation.
         *
         * @param recommendation to be written
         * @throws IOException when IO error
         */
        public void write(Recommendation<U, I> recommendation) throws IOException;
    }

    /**
     * Gets a reader for a file path.
     *
     * @param path file path
     * @return a recommendation reader
     * @throws IOException when IO error
     */
    public default Reader<U, I> getReader(String path) throws IOException {
        return getReader(new File(path));
    }
    
    /**
     * Gets a reader for a file.
     * 
     * @param file file
     * @return a recommendation reader
     * @throws IOException when IO error
     */
    public default Reader<U, I> getReader(File file) throws IOException {
        return getReader(new FileInputStream(file));
    }
    
    /**
     * Gets a reader for an input stream.
     *
     * @param in input stream
     * @return a recommendation reader
     * @throws IOException when IO error
     */
    public Reader<U, I> getReader(InputStream in) throws IOException;
    
    /**
     * Recommendation reader.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    public interface Reader<U, I> {

        /**
         * Reads all recommendations.
         *
         * @return a stream of recommendations 
         * @throws IOException when IO error
         */
        public Stream<Recommendation<U, I>> readAll() throws IOException;
    }
}
