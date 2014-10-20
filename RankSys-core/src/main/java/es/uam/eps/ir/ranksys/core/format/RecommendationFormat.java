/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.format;

import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import java.io.Closeable;
import java.io.IOException;
import java.util.stream.Stream;

/**
 *
 * @author saul
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
