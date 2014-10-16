/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.pair;

/**
 *
 * @author saul
 */
public interface ItemDistanceModel<I> {

    public double dist(I i, I j);
}
