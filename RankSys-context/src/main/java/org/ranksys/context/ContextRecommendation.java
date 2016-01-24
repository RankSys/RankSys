/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.Recommendation;
import java.util.List;

/**
 * Context-aware recommendation.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * 
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public class ContextRecommendation<U, I, C> extends Recommendation<IdObject<U, C>, I> {

    /**
     * Constructor.
     *
     * @param user user
     * @param ctx context
     * @param items recommended item-score pairs
     */
    public ContextRecommendation(U user,  C ctx, List<IdDouble<I>> items) {
        this(new IdObject<>(user, ctx), items);
    }

    protected ContextRecommendation(IdObject<U, C> userCtx, List<IdDouble<I>> items) {
        super(userCtx, items);
    }
}
