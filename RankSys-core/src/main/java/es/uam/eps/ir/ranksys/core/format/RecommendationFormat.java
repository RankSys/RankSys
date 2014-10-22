/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
import java.io.IOException;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface RecommendationFormat<U, I> {

    public Writer<U, I> getWriter(String path) throws IOException;

    public interface Writer<U, I> extends Closeable {

        public void write(Recommendation<U, I> recommendation) throws IOException;
    }

    public Reader<U, I> getReader(String path) throws IOException;
    
    public interface Reader<U, I> {

        public Stream<Recommendation<U, I>> readAll() throws IOException;
    }
}
