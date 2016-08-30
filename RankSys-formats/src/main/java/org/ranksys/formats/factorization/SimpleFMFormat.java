/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.factorization;

import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import static java.lang.Double.parseDouble;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.ranksys.javafm.FM;
import static java.lang.Integer.parseInt;
import org.ranksys.recommenders.fm.PreferenceFM;

/**
 * Factorisation machine format in zip-compressed, human readable files .
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFMFormat implements FMFormat {

    /**
     * Returns an instance of this class.
     *
     * @return an instance of SimpleFMFormat
     */
    public static SimpleFMFormat get() {
        return new SimpleFMFormat();
    }

    private SimpleFMFormat() {
    }

    @Override
    public <U, I> void save(PreferenceFM fm, OutputStream out) throws IOException {
        int N = fm.getFM().getM().length;
        int K = fm.getFM().getM()[0].length;
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("info"));
            PrintStream ps = new PrintStream(zip);
            ps.println(N);
            ps.println(K);
            ps.flush();
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("b"));
            ps = new PrintStream(zip);
            ps.println(fm.getFM().getB());
            ps.flush();
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("w"));
            saveVector(zip, fm.getFM().getW());
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("m"));
            saveMatrix(zip, fm.getFM().getM());
            zip.closeEntry();
        }
    }

    @Override
    public <U, I> PreferenceFM<U, I> load(InputStream in, FastUserIndex<U> users, FastItemIndex<I> items) throws IOException {
        int N;
        int K;
        double b;
        double[] w;
        double[][] m;
        try (ZipInputStream zip = new ZipInputStream(in)) {
            zip.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip));
            N = parseInt(reader.readLine());
            K = parseInt(reader.readLine());
            zip.closeEntry();

            zip.getNextEntry();
            reader = new BufferedReader(new InputStreamReader(zip));
            b = parseDouble(reader.readLine());
            zip.closeEntry();

            zip.getNextEntry();
            w = loadVector(zip, N);
            zip.closeEntry();

            zip.getNextEntry();
            m = loadMatrix(zip, N, K);
            zip.closeEntry();
        }

        return new PreferenceFM<>(users, items, new FM(b, w, m));
    }

    private void saveVector(OutputStream out, double[] v) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        for (int j = 0; j < v.length; j++) {
            writer.write(Double.toString(v[j]));
            writer.newLine();
        }
        writer.flush();
    }

    private void saveMatrix(OutputStream out, double[][] m) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        for (double[] pu : m) {
            for (int j = 0; j < pu.length; j++) {
                writer.write(Double.toString(pu[j]));
                if (j < pu.length - 1) {
                    writer.write('\t');
                }
            }
            writer.newLine();
        }
        writer.flush();
    }

    private double[] loadVector(InputStream in, int N) throws IOException {
        double[] v = new double[N];

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (int i = 0; i < N; i++) {
            v[i] = parseDouble(reader.readLine());
        }

        return v;
    }

    private double[][] loadMatrix(InputStream in, int N, int K) throws IOException {
        double[][] m = new double[N][K];

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (double[] mi : m) {
            String[] tokens = reader.readLine().split("\t", mi.length);
            for (int j = 0; j < mi.length; j++) {
                mi[j] = parseDouble(tokens[j]);
            }
        }

        return m;
    }

}
