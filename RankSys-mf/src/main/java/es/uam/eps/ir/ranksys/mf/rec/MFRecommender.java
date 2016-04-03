/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.mf.rec;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import es.uam.eps.ir.ranksys.mf.Factorization;
import static java.lang.Math.min;
import java.util.ArrayList;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.ranksys.core.util.tuples.Tuple2id;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Matrix factorization recommender. Scores are calculated as the inner product of user and item vectors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class MFRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final Factorization<U, I> factorization;

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param iIndex fast item index
     * @param factorization matrix factorization
     */
    public MFRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, Factorization<U, I> factorization) {
        super(uIndex, iIndex);
        this.factorization = factorization;
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        DoubleMatrix1D pu;

        pu = factorization.getUserVector(uidx2user(uidx));
        if (pu == null) {
            return new FastRecommendation(uidx, new ArrayList<>());
        }

        IntDoubleTopN topN = new IntDoubleTopN(min(maxLength, factorization.numItems()));

        DoubleMatrix1D r = factorization.getItemMatrix().zMult(pu, null);
        for (int iidx = 0; iidx < r.size(); iidx++) {
            if (filter.test(iidx)) {
                topN.add(iidx, r.getQuick(iidx));
            }
        }

        topN.sort();

        List<Tuple2id> items = topN.reverseStream()
                .collect(toList());

        return new FastRecommendation(uidx, items);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        DoubleMatrix1D pu;

        pu = factorization.getUserVector(uidx2user(uidx));
        if (pu == null) {
            return new FastRecommendation(uidx, new ArrayList<>());
        }
        
        DenseDoubleMatrix2D q = factorization.getItemMatrix();
        
        List<Tuple2id> items = candidates
                .mapToObj(iidx -> tuple(iidx, q.viewRow(iidx).zDotProduct(pu)))
                .sorted(comparingDouble(Tuple2id::v2).reversed())
                .collect(toList());
        
        return new FastRecommendation(uidx, items);
    }

}
