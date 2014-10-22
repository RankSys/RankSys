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

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.core.util.parsing.Parsers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SimpleRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Parser<U> uParser;
    private final Parser<I> iParser;
    private final Parser<Double> vParser = Parsers.dp;

    public SimpleRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public Writer getWriter(String path) throws IOException {
        return new SimpleWriter(path);
    }

    private class SimpleWriter implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public SimpleWriter(String path) throws IOException {
            this.writer = new BufferedWriter(new FileWriter(path));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
            U u = recommendation.getUser();
            for (IdDoublePair<I> pair : recommendation.getItems()) {
                writer.write(u.toString());
                writer.write("\t");
                writer.write(pair.id.toString());
                writer.write("\t");
                writer.write(Double.toString(pair.v));
                writer.newLine();
            }
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

    }

    @Override
    public Reader<U, I> getReader(String path) throws IOException {
        return new SimpleReader(path);
    }

    private class SimpleReader implements RecommendationFormat.Reader<U, I> {

        private final String path;

        public SimpleReader(String path) {
            this.path = path;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                RecommendationIterator iterator = new RecommendationIterator(reader);
                return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
            } catch (IOException ex) {
                getLogger(SimpleRecommendationFormat.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

    }

    private class RecommendationIterator implements Iterator<Recommendation<U, I>> {

        private U lastU = null;
        private I lastI;
        private Double lastS;
        private final BufferedReader reader;
        private boolean eos = false;

        public RecommendationIterator(BufferedReader reader) throws IOException {
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
            if (eos) {
                return false;
            }
            if (lastU == null) {
                String line = null;
                try {
                    line = reader.readLine();
                } catch (IOException ex) {
                    getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (line == null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return false;
                } else {
                    CharSequence[] tokens = split(line, '\t');
                    lastU = uParser.parse(tokens[0]);
                    lastI = iParser.parse(tokens[1]);
                    lastS = vParser.parse(tokens[2]);
                    return true;
                }
            } else {
                return true;
            }
        }

        @Override
        public Recommendation<U, I> next() {
            String line = null;

            List<IdDoublePair<I>> list = new ArrayList<>();

            U nextU = lastU;
            list.add(new IdDoublePair<>(lastI, lastS));
            try {
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t');
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    Double s = vParser.parse(tokens[2]);
                    if (u.equals(lastU)) {
                        list.add(new IdDoublePair<>(i, s));
                    } else {
                        lastU = u;
                        lastI = i;
                        lastS = s;
                        break;
                    }
                }
            } catch (IOException ex) {
                getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (line == null) {
                lastU = null;
                eos = true;
            }

            return new Recommendation<>(nextU, list);
        }
    }
}
