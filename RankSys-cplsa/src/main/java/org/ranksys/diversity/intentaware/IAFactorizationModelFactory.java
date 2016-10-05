package org.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.diversity.intentaware.AspectModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.mf.Factorization;

public abstract class IAFactorizationModelFactory<U, I, F> {

    private final Factorization<U, I> factorization;

    public IAFactorizationModelFactory(Factorization<U, I> factorization) {
        this.factorization = factorization;
    }

    public abstract IntentModel<U, I, F> getIntentModel();

    public abstract AspectModel<U, I, F> getAspectModel();

    protected Factorization<U, I> getFactorization() {
        return this.factorization;
    }
}
