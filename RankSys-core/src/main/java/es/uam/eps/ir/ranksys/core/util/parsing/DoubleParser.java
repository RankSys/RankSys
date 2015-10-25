/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

/**
 * Parses a CharSequence to a double
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface DoubleParser {

    /**
     * Parses a CharSequence into a double.
     *
     * @param from string to be parsed
     * @return parsed double
     */
    public double parse(CharSequence from);
    
    /**
     * Default double parser
     */
    public static final DoubleParser ddp = (token) -> Double.parseDouble(token.toString());
}
