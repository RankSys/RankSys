/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.mf.als;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.LUDecompositionQuick;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.als.ALSFactorizer;

/**
 * ALS factorizer for Personalized Ranking
 *
 * G. Takács, D. Tikk. Alternating Least Squares for Personalized Ranking. RecSys 2012.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class RankALSFactorizer<U, I> extends ALSFactorizer<U, I> {

    /**
     * Item importance weighting
     */
    private final Boolean itemImportanceWeighting;

    /**
     * Item importance
     */
    private DoubleMatrix1D s;

    /**
     * \sum_j s_j
     */
    private double one_tilde;

    /**
     * \sum_j s_j * q_j
     */
    private DoubleMatrix1D q_tilde;

    /**
     * \sum_j s_j * q_j * q_j^T
     */
    private DoubleMatrix2D A_tilde;

    /**
     * \sum_j s_j * r_{uj}
     */
    private DoubleMatrix1D r_tilde;

    /**
     * \sum_i c_{ui} * r_{ui}
     */
    private DoubleMatrix1D r_bar;

    /**
     * \sum_i c_{ui}
     */
    private DoubleMatrix1D one_bar;

    /**
     * Constructor. Items are equally important.
     *
     * @param numIter number of iterations
     */
    public RankALSFactorizer(int numIter) {
        super(numIter);
        this.itemImportanceWeighting = false;
    }

    /**
     * Constructor.
     *
     * @param numIter number of iterations
     * @param itemImportanceWeighting determines if items should be equally weighted or not
     */
    public RankALSFactorizer(int numIter, boolean itemImportanceWeighting) {
        super(numIter);
        this.itemImportanceWeighting = itemImportanceWeighting;
    }

    @Override
    protected double error(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data) {
        return data.getUidxWithPreferences().parallel().mapToDouble(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);
            DoubleMatrix1D su = q.zMult(pu, null);
            return data.getUidxPreferences(uidx).mapToDouble(iv -> {
                double rui = iv.v2;
                double sui = su.getQuick(iv.v1);
                double diffi = rui - sui;
                return data.getUidxPreferences(uidx).mapToDouble(jv -> {
                    double ruj = jv.v2;
                    double suj = su.getQuick(jv.v1);
                    double diffj = ruj - suj;
                    return s.getQuick(jv.v1) * (diffi - diffj) * (diffi - diffj);
                }).sum();
            }).sum();
        }).sum();
    }

    @Override
    public void factorize(Factorization<U, I> factorization, FastPreferenceData<U, I> data) {
        // These can be calculated before we start factorizing
        s = new DenseDoubleMatrix1D(data.numItems());
        one_tilde = data.getAllIidx().mapToDouble(iidx -> {
            double si = itemImportanceWeighting ? data.numUsers(iidx) : 1;
            s.setQuick(iidx, si);
            return si;
        }).sum();

        r_tilde = new DenseDoubleMatrix1D(data.numUsers());
        r_bar = new DenseDoubleMatrix1D(data.numUsers());
        one_bar = new DenseDoubleMatrix1D(data.numUsers());
        data.getUidxWithPreferences().parallel().forEach(uidx -> {
            one_bar.setQuick(uidx, data.numItems(uidx));
            data.getUidxPreferences(uidx).forEach(iv -> {
                int iidx = iv.v1();
                double rui = iv.v2();

                double si = s.getQuick(iidx);
                r_tilde.setQuick(uidx, r_tilde.getQuick(uidx) + si * rui);
                r_bar.setQuick(uidx, r_bar.getQuick(uidx) + rui);
            });
        });

        super.factorize(factorization, data);
    }

    @Override
    protected void set_minP(DenseDoubleMatrix2D p, DenseDoubleMatrix2D q, FastPreferenceData<U, I> data) {
        final int K = p.columns();

        q_tilde = new DenseDoubleMatrix1D(K);
        A_tilde = new DenseDoubleMatrix2D(K, K);
        data.getAllIidx().forEach(jidx -> {
            DoubleMatrix1D qj = q.viewRow(jidx);
            double sj = s.getQuick(jidx);
            q_tilde.assign(qj, (x, y) -> x + y * sj);
            A_tilde.assign(Algebra.DEFAULT.multOuter(qj, qj, null), (x, y) -> x + y * sj);
        });

        data.getUidxWithPreferences().parallel().forEach(uidx -> {
            DoubleMatrix2D A_bar = new DenseDoubleMatrix2D(K, K);
            DoubleMatrix1D q_bar = new DenseDoubleMatrix1D(K);
            DoubleMatrix1D b_bar = new DenseDoubleMatrix1D(K);
            DoubleMatrix1D b_tilde = new DenseDoubleMatrix1D(K);

            data.getUidxPreferences(uidx).forEach(iv -> {
                int iidx = iv.v1();
                double rui = iv.v2();
                DoubleMatrix1D qi = q.viewRow(iidx);

                DoubleMatrix2D qq = Algebra.DEFAULT.multOuter(qi, qi, null);
                A_bar.assign(qq, (x, y) -> x + y);
                q_bar.assign(qi, (x, y) -> x + y);
                b_bar.assign(qi, (x, y) -> x + y * rui);

                double si = s.getQuick(iidx);
                b_tilde.assign(qi, (x, y) -> x + y * si * rui);
            });

            DoubleMatrix2D A = new DenseDoubleMatrix2D(K, K);
            DoubleMatrix1D b = new DenseDoubleMatrix1D(K);

            A.assign(A_bar, (x, y) -> x + one_tilde * y);
            A.assign(Algebra.DEFAULT.multOuter(q_bar, q_tilde, null), (x, y) -> x - y);
            A.assign(Algebra.DEFAULT.multOuter(q_tilde, q_bar, null), (x, y) -> x - y);
            A.assign(A_tilde, (x, y) -> x + one_bar.getQuick(uidx) * y);

            b.assign(b_bar, (x, y) -> x + y * one_tilde);
            b.assign(q_bar, (x, y) -> x - y * r_tilde.getQuick(uidx));
            b.assign(q_tilde, (x, y) -> x - r_bar.getQuick(uidx) * y);
            b.assign(b_tilde, (x, y) -> x + one_bar.getQuick(uidx) * y);

            LUDecompositionQuick lu = new LUDecompositionQuick(0);
            lu.decompose(A);
            lu.solve(b);
            p.viewRow(uidx).assign(b);
        });
    }

    @Override
    protected void set_minQ(DenseDoubleMatrix2D q, DenseDoubleMatrix2D p, FastPreferenceData<U, I> data) {
        final int K = q.columns();

        q_tilde = new DenseDoubleMatrix1D(K);
        A_tilde = new DenseDoubleMatrix2D(K, K);
        data.getAllIidx().forEach(jidx -> {
            DoubleMatrix1D qj = q.viewRow(jidx);
            double sj = s.getQuick(jidx);
            q_tilde.assign(qj, (x, y) -> x + y * sj);
        });

        DoubleMatrix2D A_bar_bar = new DenseDoubleMatrix2D(K, K);
        DoubleMatrix1D p_bar_bar_2 = new DenseDoubleMatrix1D(K);
        DoubleMatrix1D p_bar_bar_3 = new DenseDoubleMatrix1D(K);

        data.getUidxWithPreferences().forEach(uidx -> {
            DoubleMatrix1D pu = p.viewRow(uidx);

            DoubleMatrix1D q_bar = new DenseDoubleMatrix1D(K);
            data.getUidxPreferences(uidx).forEach(iv -> {
                q_bar.assign(q.viewRow(iv.v1), (x, y) -> x + y);
            });

            DoubleMatrix2D p_p = Algebra.DEFAULT.multOuter(pu, pu, null);
            A_bar_bar.assign(p_p, (x, y) -> x + y * one_bar.getQuick(uidx));
            p_bar_bar_2.assign(p_p.zMult(q_bar, null), (x, y) -> x + y);
            p_bar_bar_3.assign(pu, (x, y) -> x + y * r_bar.getQuick(uidx));
        });

        data.getIidxWithPreferences().parallel().forEach(iidx -> {
            double si = s.getQuick(iidx);

            DoubleMatrix2D A_bar = new DenseDoubleMatrix2D(K, K);
            DoubleMatrix1D b_bar = new DenseDoubleMatrix1D(K);
            DoubleMatrix1D p_bar_bar_1 = new DenseDoubleMatrix1D(K);
            DoubleMatrix1D b_bar_bar = new DenseDoubleMatrix1D(K);

            data.getIidxPreferences(iidx).forEach(uv -> {
                int uidx = uv.v1();
                DoubleMatrix1D pu = p.viewRow(uidx);
                double rui = uv.v2();

                A_bar.assign(Algebra.DEFAULT.multOuter(pu, pu, null), (x, y) -> x + y);

                b_bar.assign(pu, (x, y) -> x + y * rui);
                p_bar_bar_1.assign(pu, (x, y) -> x + y * r_tilde.getQuick(uidx));
                b_bar_bar.assign(pu, (x, y) -> x + y * rui * one_bar.getQuick(uidx));
            });

            DoubleMatrix2D A = new DenseDoubleMatrix2D(K, K);
            DoubleMatrix1D b = new DenseDoubleMatrix1D(K);

            A.assign(A_bar, (x, y) -> x + y * one_tilde);
            A.assign(A_bar_bar, (x, y) -> x + y * si);

            b.assign(Algebra.DEFAULT.mult(A_bar, q_tilde), (x, y) -> x + y);
            b.assign(b_bar, (x, y) -> x + y * one_tilde);
            b.assign(p_bar_bar_1, (x, y) -> x - y);
            b.assign(p_bar_bar_2, (x, y) -> x + y * si);
            b.assign(p_bar_bar_3, (x, y) -> x - y * si);
            b.assign(b_bar_bar, (x, y) -> x + y * si);

            LUDecompositionQuick lu = new LUDecompositionQuick(0);
            lu.decompose(A);
            lu.solve(b);
            q.viewRow(iidx).assign(b);
        });
    }
}
