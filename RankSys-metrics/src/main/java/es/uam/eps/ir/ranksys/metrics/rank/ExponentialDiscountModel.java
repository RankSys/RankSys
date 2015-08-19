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
package es.uam.eps.ir.ranksys.metrics.rank;

/**
 * Exponential ranking discount: disc(k) = base^k.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ExponentialDiscountModel implements RankingDiscountModel {

    private final double base;

    /**
     * Constructor.
     *
     * @param base base of the exponent
     */
    public ExponentialDiscountModel(double base) {
        this.base = base;
    }

    /**
     * Discount to be applied at a given position.
     *
     * @param k position in the recommendation list starting from 0
     * @return discount to be applied for the given rank position
     */
    @Override
    public double disc(int k) {
        return Math.pow(base, k);
    }

}
