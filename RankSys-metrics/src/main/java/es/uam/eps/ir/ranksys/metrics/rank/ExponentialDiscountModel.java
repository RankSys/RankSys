/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics.rank;

/**
 *
 * @author saul
 */
public class ExponentialDiscountModel implements RankingDiscountModel {

    private final double base;

    public ExponentialDiscountModel(double base) {
        this.base = base;
    }

    @Override
    public double disc(int k) {
        return Math.pow(base, k);
    }

}
