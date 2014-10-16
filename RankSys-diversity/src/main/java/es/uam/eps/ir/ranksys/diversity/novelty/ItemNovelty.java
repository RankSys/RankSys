/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.diversity.novelty;

/**
 *
 * @author saul
 */
public abstract class ItemNovelty<U, I> {
    public abstract double novelty(I i, U u);
}
