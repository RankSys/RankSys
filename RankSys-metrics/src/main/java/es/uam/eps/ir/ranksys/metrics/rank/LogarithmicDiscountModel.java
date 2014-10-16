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
public class LogarithmicDiscountModel implements RankingDiscountModel{

    @Override
    public double disc(int k) {
        return 1 / Math.log(k + 2.0) * Math.log(2.0);
    }
    
}
