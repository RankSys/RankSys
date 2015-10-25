/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.rank;

/**
 * Logarithmic discount: disc(k) = 1 / log_2(k + 2).
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class LogarithmicDiscountModel implements RankingDiscountModel{

    @Override
    public double disc(int k) {
        return 1 / Math.log(k + 2.0) * Math.log(2.0);
    }
    
}
