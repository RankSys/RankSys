/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.preference.FastPointWisePreferenceData;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public interface ContextFastPreferenceData<U, I, C> extends ContextPreferenceData<U, I, C>, FastPointWisePreferenceData<U, I> {

    @Override
    public Stream<IdxPrefCtx<C>> getUidxPreferences(int uidx);

    @Override
    public Stream<IdxPrefCtx<C>> getIidxPreferences(int iidx);

    @Override
    public default IdPrefCtx<I, C> getPreference(U u, I i) {
        IdxPrefCtx pref = getPreference(user2uidx(u), item2iidx(i));
        
        return new IdPrefCtx<>(i, pref.v, pref.cs);
    }

    @Override
    public IdxPrefCtx<C> getPreference(int uidx, int iidx);
    
    public class IdxPrefCtx<C> extends IdxPref {

        public List<C> cs;

        public IdxPrefCtx(int idx, double v, List<C> cs) {
            super(idx, v);
            this.cs = cs;
        }

        public IdxPref refill(int idx, double v, List<C> cs) {
            this.idx = idx;
            this.v = v;
            this.cs = cs;
            return this;
        }

    }
}
