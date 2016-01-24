/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rec;

import org.ranksys.context.ContextRecommendation;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.rec.AbstractRecommender;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public abstract class AbstractContextRecommender<U, I, C> extends AbstractRecommender<IdObject<U, C>, I> implements ContextRecommender<U, I, C> {

    @Override
    public ContextRecommendation<U, I, C> getRecommendation(final U u, final C c, int maxLength) {
        return getRecommendation(u, c, maxLength, i -> true);
    }

    @Override
    public ContextRecommendation<U, I, C> getRecommendation(final U u, final C c, Stream<I> candidates) {
        Set<I> set = candidates.collect(Collectors.toSet());

        return getRecommendation(u, c, 0, item -> set.contains(item));
    }

}
