/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

/**
 *
 * @author saul
 */
public interface RelevanceModel<U, I> {

    public UserRelevanceModel getUserModel(U user);

    public interface UserRelevanceModel<U, I> {

        public boolean isRelevant(I item);

        public double gain(I item);
    }
}
