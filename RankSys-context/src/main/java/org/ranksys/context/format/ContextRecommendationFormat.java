/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.format;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.core.format.SimpleRecommendationFormat;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public class ContextRecommendationFormat<U, I, C> implements RecommendationFormat<IdObject<U, C>, I> {

    private final Parser<U> up;
    private final Parser<I> ip;
    private final DoubleParser vp;
    private final Function<C, String> contextWriter;
    private final Function<String, C> contextReader;

    public ContextRecommendationFormat(Parser<U> up, Parser<I> ip, DoubleParser vp, Function<C, String> contextWriter, Function<String, C> contextReader) {
        this.up = up;
        this.ip = ip;
        this.vp = vp;
        this.contextWriter = contextWriter;
        this.contextReader = contextReader;
    }

    @Override
    public Writer<IdObject<U, C>, I> getWriter(OutputStream out) throws IOException {
        return new ContextWriter(out);
    }

    @Override
    public Reader<IdObject<U, C>, I> getReader(InputStream in) throws IOException {
        return new ContextReader(in);
    }

    private class ContextWriter implements Writer<IdObject<U, C>, I> {

        private final BufferedWriter writer;

        public ContextWriter(OutputStream out) throws IOException {
            this.writer = new BufferedWriter(new OutputStreamWriter(out));
        }

        @Override
        public void write(Recommendation<IdObject<U, C>, I> recommendation) throws IOException {
            U u = recommendation.getUser().id;
            C c = recommendation.getUser().v;
            for (IdDouble<I> pair : recommendation.getItems()) {
                writer.write(u.toString());
                writer.write("\t");
                writer.write(pair.id.toString());
                writer.write("\t");
                writer.write(Double.toString(pair.v));
                writer.write("\t");
                writer.write(contextWriter.apply(c));
                writer.newLine();
            }
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }
    }

    private class ContextReader implements Reader<IdObject<U, C>, I> {

        private final InputStream in;

        public ContextReader(InputStream in) {
            this.in = in;
        }

        @Override
        public Stream<Recommendation<IdObject<U, C>, I>> readAll() throws IOException {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                RecommendationIterator iterator = new RecommendationIterator(reader);
                return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
            } catch (IOException ex) {
                getLogger(SimpleRecommendationFormat.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    private class RecommendationIterator implements Iterator<Recommendation<IdObject<U, C>, I>> {

        private U lastU = null;
        private I lastI;
        private double lastS;
        private C lastC;
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
                    CharSequence[] tokens = split(line, '\t', 4);
                    lastU = up.parse(tokens[0]);
                    lastI = ip.parse(tokens[1]);
                    lastS = vp.parse(tokens[2]);
                    lastC = contextReader.apply(tokens[3].toString());
                    return true;
                }
            } else {
                return true;
            }
        }

        @Override
        public Recommendation<IdObject<U, C>, I> next() {
            String line = null;

            List<IdDouble<I>> list = new ArrayList<>();

            U nextU = lastU;
            C nextC = lastC;
            list.add(new IdDouble<>(lastI, lastS));
            try {
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t', 4);
                    U u = up.parse(tokens[0]);
                    I i = ip.parse(tokens[1]);
                    double s = vp.parse(tokens[2]);
                    C c = contextReader.apply(tokens[3].toString());
                    if (u.equals(lastU) && c.equals(lastC)) {
                        list.add(new IdDouble<>(i, s));
                    } else {
                        lastU = u;
                        lastI = i;
                        lastS = s;
                        lastC = c;
                        break;
                    }
                }
            } catch (IOException ex) {
                getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (line == null) {
                lastU = null;
                lastC = null;
                eos = true;
            }

            return new Recommendation<>(new IdObject<>(nextU, nextC), list);
        }
    }

}
