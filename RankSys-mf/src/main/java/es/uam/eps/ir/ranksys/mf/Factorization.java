/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.mf;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.dp;
import static es.uam.eps.ir.ranksys.core.util.parsing.Parsers.ip;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
 * Matrix factorization.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class Factorization<U, I> implements FastItemIndex<I>, FastUserIndex<U> {

    /**
     * user matrix
     */
    protected final DenseDoubleMatrix2D userMatrix;

    /**
     * item matrix
     */
    protected final DenseDoubleMatrix2D itemMatrix;

    /**
     * dimensionality of the vector space
     */
    protected final int K;

    /**
     * user index
     */
    protected final FastUserIndex<U> uIndex;

    /**
     * item index
     */
    protected final FastItemIndex<I> iIndex;

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param iIndex fast item index
     * @param K dimension of the latent feature space
     * @param initFunction function to initialize the cells of the matrices
     */
    public Factorization(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, int K, DoubleFunction initFunction) {
        this.userMatrix = new DenseDoubleMatrix2D(uIndex.numUsers(), K);
        this.userMatrix.assign(initFunction);
        this.itemMatrix = new DenseDoubleMatrix2D(iIndex.numItems(), K);
        this.itemMatrix.assign(initFunction);
        this.K = K;
        this.uIndex = uIndex;
        this.iIndex = iIndex;
    }

    /**
     * Constructor for stored factorizations.
     *
     * @param uIndex fast user index
     * @param iIndex fast item index
     * @param userMatrix user matrix
     * @param itemMatrix item matrix
     * @param K dimension of the latent feature space
     */
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

    /**
     * Returns the row of the user matrix corresponding to the given user.
     *
     * @param u user
     * @return row of the user matrix
     */
    public DoubleMatrix1D getUserVector(U u) {
        int uidx = user2uidx(u);
        if (uidx < 0) {
            return null;
        } else {
            return userMatrix.viewRow(uidx);
        }
    }

    /**
     * Returns the row of the item matrix corresponding to the given item.
     *
     * @param i item
     * @return row of the item matrix
     */
    public DoubleMatrix1D getItemVector(I i) {
        int iidx = item2iidx(i);
        if (iidx < 0) {
            return null;
        } else {
            return itemMatrix.viewRow(iidx);
        }
    }

    /**
     * Returns the whole user matrix.
     *
     * @return the whole user matrix
     */
    public DenseDoubleMatrix2D getUserMatrix() {
        return userMatrix;
    }

    /**
     * Returns the whole item matrix.
     *
     * @return the whole item matrix
     */
    public DenseDoubleMatrix2D getItemMatrix() {
        return itemMatrix;
    }

    /**
     * Returns the dimension of the latent feature space.
     *
     * @return the dimension of the latent feature space
     */
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

    /**
     * Saves this matrix factorization to a compressed output stream.
     *
     * @param out output stream
     * @throws IOException when IO error
     */
    public void save(OutputStream out) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("info"));
            PrintStream ps = new PrintStream(zip);
            ps.println(numUsers());
            ps.println(numItems());
            ps.println(K);
            ps.flush();
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("userMatrix"));
            saveDenseDoubleMatrix2D(zip, userMatrix);
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("itemMatrix"));
            saveDenseDoubleMatrix2D(zip, itemMatrix);
            zip.closeEntry();
        }
    }

    /**
     * Loads a matrix from a compressed input stream.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param in input stream
     * @param uIndex fast user index
     * @param iIndex fast item index
     * @return a factorization
     * @throws IOException when IO error
     */
    public static <U, I> Factorization<U, I> load(InputStream in, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) throws IOException {
        int K;
        DenseDoubleMatrix2D userMatrix;
        DenseDoubleMatrix2D itemMatrix;
        try (ZipInputStream zip = new ZipInputStream(in)) {
            zip.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip));
            int numUsers = ip.parse(reader.readLine());
            int numItems = ip.parse(reader.readLine());
            K = ip.parse(reader.readLine());
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
