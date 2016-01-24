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
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public interface ContextPreferenceData<U, I, C> extends PointWisePreferenceData<U, I> {

    @Override
    public Stream<IdPrefCtx<I, C>> getUserPreferences(U u);

    @Override
    public Stream<IdPrefCtx<U, C>> getItemPreferences(I i);

    @Override
    public Optional<IdPrefCtx<I, C>> getPreference(U u, I i);

    public int getContextSize();

    public class IdPrefCtx<I, C> extends IdPref<I> {

        public List<C> cs;

        public IdPrefCtx(I id, double v, List<C> cs) {
            super(id, v);
            this.cs = cs;
        }

        public IdPrefCtx<I, C> refill(I id, double v, List<C> cs) {
            this.id = id;
            this.v = v;
            this.cs = cs;
            return this;
        }

    }
}
