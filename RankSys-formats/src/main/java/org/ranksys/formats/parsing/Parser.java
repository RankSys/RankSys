/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.parsing;

import java.util.function.Function;

/**
 * Interface to parse a CharSequence.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <T> type of the resulting object
 */
public interface Parser<T> extends Function<CharSequence, T> {

    /**
     * Parse a CharSequence into an object of type T.
     *
     * @param from input string
     * @return the object resulting from the parsing
     */
    public T parse(CharSequence from);

    @Override
    public default T apply(CharSequence from) {
        return parse(from);
    }

}
