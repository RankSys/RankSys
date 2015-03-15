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
