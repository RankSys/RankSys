/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util;

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
