/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics.rel;

import org.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;

/**
 * Relevance model in which every item is judged as relevant.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class NoRelevanceModel<U, I> extends RelevanceModel<U, I> {

    private final UserNoRelevanceModel rel = new UserNoRelevanceModel();

    /**
     * Constructor. No need to cache anything here.
     *
     */
    public NoRelevanceModel() {
        super();
    }

    @Override
    protected UserRelevanceModel<U, I> get(U user) {
        return rel;
    }

    @Override
    public UserRelevanceModel<U, I> getModel(U u) {
        return rel;
    }

    private class UserNoRelevanceModel implements UserRelevanceModel<U, I> {

        @Override
        public boolean isRelevant(I item) {
            return true;
        }

        @Override
        public double gain(I item) {
            return 1.0;
        }

    }

}
