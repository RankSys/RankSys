/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.diversity.intentaware.AspectModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.mf.Factorization;

/**
 * Factory class to create factorization-based intent-aware models.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public abstract class IAFactorizationModelFactory<U, I, F> {

    private final Factorization<U, I> factorization;

    /**
     * Creates the factorization-based models factory.
     *
     * @param factorization factorization model
     */
    public IAFactorizationModelFactory(Factorization<U, I> factorization) {
        this.factorization = factorization;
    }

    /**
     * Returns factorization-based intent model.
     *
     * @return intent model
     */
    public abstract IntentModel<U, I, F> getIntentModel();

    /**
     * Returns factorization-based aspect model.
     *
     * @return aspect model
     */
    public abstract AspectModel<U, I, F> getAspectModel();

    /**
     *
     * Returns factorization model.
     *
     * @return factorization model
     */
    protected Factorization<U, I> getFactorization() {
        return this.factorization;
    }
}
