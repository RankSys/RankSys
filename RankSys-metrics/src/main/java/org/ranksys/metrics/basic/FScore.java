/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.metrics.basic.Precision;
import es.uam.eps.ir.ranksys.metrics.basic.Recall;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;

/**
 * F-score or harmonic mean of precision and recall.
 *
 * @param <U> user type
 * @param <I> item type
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class FScore<U, I> extends HarmonicMean<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff   recommendation list cutoff
     * @param relModel relevance model
     */
    public FScore(int cutoff, IdealRelevanceModel<U, I> relModel) {
        super(new Precision<>(cutoff, relModel), new Recall<>(cutoff, relModel));
    }
}
