/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.core.util;

import java.util.function.Supplier;

/**
 * Lazy initializer implementing the Supplier interface.
 *
 * Adapted from Apache Commons' LazyInitializer.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <T> type of the initialized object
 */
public class Lazy<T> implements Supplier<T> {

    private volatile T object;
    private final Supplier<T> supplier;

    /**
     * Constructor.
     *
     * @param supplier functions that supplies the value to be lazily initalized
     */
    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        T result = object;

        if (result == null) {
            synchronized (this) {
                result = object;
                if (result == null) {
                    object = result = supplier.get();
                }
            }
        }

        return result;
    }

}
