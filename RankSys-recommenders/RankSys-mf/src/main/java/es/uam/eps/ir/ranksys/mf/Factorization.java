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
package es.uam.eps.ir.ranksys.mf;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.dp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.ip;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Factorization<U, I> implements FastItemIndex<I>, FastUserIndex<U> {

    private final DenseDoubleMatrix2D userMatrix;
    private final DenseDoubleMatrix2D itemMatrix;
    private final int K;
    private final FastUserIndex<U> uIndex;
    private final FastItemIndex<I> iIndex;

    public Factorization(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, int K, DoubleFunction initFunction) {
        this.userMatrix = new DenseDoubleMatrix2D(uIndex.numUsers(), K);
        this.userMatrix.assign(initFunction);
        this.itemMatrix = new DenseDoubleMatrix2D(iIndex.numItems(), K);
        this.itemMatrix.assign(initFunction);
        this.K = K;
        this.uIndex = uIndex;
        this.iIndex = iIndex;
    }

    protected Factorization(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, DenseDoubleMatrix2D userMatrix, DenseDoubleMatrix2D itemMatrix, int K) {
        this.userMatrix = userMatrix;
        this.itemMatrix = itemMatrix;
        this.K = K;
        this.uIndex = uIndex;
        this.iIndex = iIndex;
    }

    @Override
    public int numUsers() {
        return uIndex.numUsers();
    }

    @Override
    public int user2uidx(U u) {
        return uIndex.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uIndex.uidx2user(uidx);
    }

    @Override
    public int numItems() {
        return iIndex.numItems();
    }

    @Override
    public int item2iidx(I i) {
        return iIndex.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iIndex.iidx2item(iidx);
    }

    @Override
    public boolean containsUser(U u) {
        return uIndex.containsUser(u);
    }

    @Override
    public boolean containsItem(I i) {
        return iIndex.containsItem(i);
    }

    public DoubleMatrix1D getUserVector(U u) {
        int uidx = user2uidx(u);
        if (uidx < 0) {
            return null;
        } else {
            return userMatrix.viewRow(uidx);
        }
    }

    public DoubleMatrix1D getItemVector(I i) {
        int iidx = item2iidx(i);
        if (iidx < 0) {
            return null;
        } else {
            return itemMatrix.viewRow(iidx);
        }
    }

    public DenseDoubleMatrix2D getUserMatrix() {
        return userMatrix;
    }

    public DenseDoubleMatrix2D getItemMatrix() {
        return itemMatrix;
    }

    public int getK() {
        return K;
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
                mi[j] = dp.parse(tokens[j]);
            }
        }

        return new DenseDoubleMatrix2D(m);
    }

    public void save(String basename) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(basename))) {
            zip.putNextEntry(new ZipEntry("info"));
            PrintStream out = new PrintStream(zip);
            out.println(numUsers());
            out.println(numItems());
            out.println(K);
            out.flush();
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("userMatrix"));
            saveDenseDoubleMatrix2D(zip, userMatrix);
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("itemMatrix"));
            saveDenseDoubleMatrix2D(zip, itemMatrix);
            zip.closeEntry();
        }
    }

    public static <U, I, V> Factorization<U, I> load(String basename, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, Parser<U> uParser, Parser<I> iParser) throws IOException {
        BufferedReader in;
        int K;
        DenseDoubleMatrix2D userMatrix;
        DenseDoubleMatrix2D itemMatrix;
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(basename))) {
            zip.getNextEntry();
            in = new BufferedReader(new InputStreamReader(zip));
            int numUsers = ip.parse(in.readLine());
            int numItems = ip.parse(in.readLine());
            K = ip.parse(in.readLine());
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
