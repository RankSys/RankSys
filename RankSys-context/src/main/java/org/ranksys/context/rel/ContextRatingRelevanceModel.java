package org.ranksys.context.rel;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.ranksys.context.pref.ContextPreferenceData.IdPrefCtx;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class ContextRatingRelevanceModel<U, I, C> extends RelevanceModel<IdObject<U, C>, I> {

    private final PreferenceData<U, I> testData;

    /**
     * Constructor
     *
     * @param testData test subset of the preferences
     */
    public ContextRatingRelevanceModel(PreferenceData<U, I> testData) {
        super();
        this.testData = testData;
    }

    @Override
    protected UserRelevanceModel<IdObject<U, C>, I> get(IdObject<U, C> userCtx) {
        return new UserContextBinaryRelevanceModel(userCtx);
    }

    private class UserContextBinaryRelevanceModel implements UserRelevanceModel<IdObject<U, C>, I> {

        private final Object2DoubleMap<I> userRatings;

        public UserContextBinaryRelevanceModel(IdObject<U, C> userCtx) {
            this.userRatings = new Object2DoubleOpenHashMap<>();
            testData.getUserPreferences(userCtx.id)
                    .filter(p -> ((IdPrefCtx<I, C>) p).cs.contains(userCtx.v))
                    .forEach(p -> userRatings.put(p.id, p.v));
        }

        @Override
        public boolean isRelevant(I item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double gain(I item) {
            return userRatings.getDouble(item);
        }

    }
}
