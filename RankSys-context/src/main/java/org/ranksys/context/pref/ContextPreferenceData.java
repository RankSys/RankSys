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

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.ranksys.core.preference.PointWisePreferenceData;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
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
