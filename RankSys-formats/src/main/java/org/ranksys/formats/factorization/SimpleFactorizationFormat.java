/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.factorization;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.mf.Factorization;
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
import static org.ranksys.core.util.FastStringSplitter.split;
import static java.lang.Integer.parseInt;

/**
 * Zip-compressed, human readable matrix factorisation format.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFactorizationFormat implements FactorizationFormat {

    /**
     * Returns an instance of this class.
     *
     * @return an instance of SimpleFactorizationFormat
     */
    public static SimpleFactorizationFormat get() {
        return new SimpleFactorizationFormat();
    }

    private SimpleFactorizationFormat() {
    }

    private static void saveDenseDoubleMatrix2D(OutputStream stream, DenseDoubleMatrix2D matrix) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream));
        double[][] m = matrix.toArray();
        for (double[] pu : m) {
            for (int j = 0; j < pu.length - 1; j++) {
                out.write(Double.toString(pu[j]));
                out.write('\t');
            }
            out.write(Double.toString(pu[pu.length - 1]));
            out.newLine();
        }
        out.flush();
    }

    private static DenseDoubleMatrix2D loadDenseDoubleMatrix2D(InputStream stream, int rows, int columns) throws IOException {
        double[][] m = new double[rows][columns];

        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        for (double[] mi : m) {
            CharSequence[] tokens = split(in.readLine(), '\t', mi.length);
            for (int j = 0; j < mi.length; j++) {
                mi[j] = parseDouble(tokens[j].toString());
            }
        }

        return new DenseDoubleMatrix2D(m);
    }

    @Override
    public <U, I> void save(Factorization<U, I> factorization, OutputStream out) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("info"));
            PrintStream ps = new PrintStream(zip);
            ps.println(factorization.numUsers());
            ps.println(factorization.numItems());
            ps.println(factorization.getK());
            ps.flush();
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("userMatrix"));
            saveDenseDoubleMatrix2D(zip, factorization.getUserMatrix());
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("itemMatrix"));
            saveDenseDoubleMatrix2D(zip, factorization.getItemMatrix());
            zip.closeEntry();
        }
    }

    @Override
    public <U, I> Factorization<U, I> load(InputStream in, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) throws IOException {
        int K;
        DenseDoubleMatrix2D userMatrix;
        DenseDoubleMatrix2D itemMatrix;
        try (ZipInputStream zip = new ZipInputStream(in)) {
            zip.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip));
            int numUsers = parseInt(reader.readLine());
            int numItems = parseInt(reader.readLine());
            K = parseInt(reader.readLine());
            zip.closeEntry();

            zip.getNextEntry();
            userMatrix = loadDenseDoubleMatrix2D(zip, numUsers, K);
            zip.closeEntry();

            zip.getNextEntry();
            itemMatrix = loadDenseDoubleMatrix2D(zip, numItems, K);
            zip.closeEntry();
        }

        return new Factorization<>(uIndex, iIndex, userMatrix, itemMatrix, K);
    }

}
