/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.ranksys.core.preference.PointWisePreferenceData;

/**
 * Context-aware preference data. Each user-item preference is accompanied by
 * the list of context in which the interaction occurred.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * 
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public interface ContextPreferenceData<U, I, C> extends PointWisePreferenceData<U, I> {

    @Override
    public Stream<IdPrefCtx<I, C>> getUserPreferences(U u);

    @Override
    public Stream<IdPrefCtx<U, C>> getItemPreferences(I i);

    @Override
    public Optional<IdPrefCtx<I, C>> getPreference(U u, I i);

    /**
     * Returns the dimensionality of the context.
     *
     * @return dimensionality of the context
     */
    public int getContextSize();

    /**
     * A context-aware preference.
     *
     * @param <I> item type
     * @param <C> context type
     */
    public class IdPrefCtx<I, C> extends IdPref<I> {

        /**
         * List of contexts for which there was an interaction with item id.
         */
        public List<C> cs;

        /**
         * Constructor.
         *
         * @param id item
         * @param v value
         * @param cs list of contexts
         */
        public IdPrefCtx(I id, double v, List<C> cs) {
            super(id, v);
            this.cs = cs;
        }

        /**
         * Re-use this object with new values for its fields.
         *
         * @param id item
         * @param v value
         * @param cs list of contexts
         * @return this object with the new values
         */
        public IdPrefCtx<I, C> refill(I id, double v, List<C> cs) {
            this.id = id;
            this.v = v;
            this.cs = cs;
            return this;
        }

    }
}
