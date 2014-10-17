/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

import java.util.Set;

/**
 *
 * @author saul
 */
public interface IdealRelevanceModel<U, I> extends RelevanceModel<U, I> {

    @Override
    public UserIdealRelevanceModel getUserModel(U user);

    public interface UserIdealRelevanceModel<U, I> extends UserRelevanceModel<U, I> {

        public Set<I> getRelevantItems();

    }
}
