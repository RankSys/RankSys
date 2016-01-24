/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rec;

import org.ranksys.context.ContextRecommendation;
import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public interface ContextRecommender<U, I, C> extends Recommender<IdObject<U, C>, I> {

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, int maxLength) {
        List<IdDouble<I>> items = getRecommendation(new IdObject<>(u, c), maxLength).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, int maxLength, Predicate<I> filter) {
        List<IdDouble<I>> items = getRecommendation(new IdObject<>(u, c), maxLength, filter).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, Stream<I> candidates) {
        List<IdDouble<I>> items = getRecommendation(new IdObject<>(u, c), candidates).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

}
