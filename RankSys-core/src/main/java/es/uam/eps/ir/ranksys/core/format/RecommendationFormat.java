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
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface RecommendationFormat<U, I> {

    public default Writer<U, I> getWriter(String path) throws IOException {
        return getWriter(new File(path));
    }
    
    public default Writer<U, I> getWriter(File file) throws IOException {
        return getWriter(new FileOutputStream(file));
    }
    
    public Writer<U, I> getWriter(OutputStream out) throws IOException;

    public interface Writer<U, I> extends Closeable {

        public void write(Recommendation<U, I> recommendation) throws IOException;
    }

    public default Reader<U, I> getReader(String path) throws IOException {
        return getReader(new File(path));
    }
    
    public default Reader<U, I> getReader(File file) throws IOException {
        return getReader(new FileInputStream(file));
    }
    
    public Reader<U, I> getReader(InputStream in) throws IOException;
    
    public interface Reader<U, I> {

        public Stream<Recommendation<U, I>> readAll() throws IOException;
    }
}
