/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.nn.user.neighborhood;

import es.uam.eps.ir.ranksys.nn.neighborhood.InvertedNeighborhood;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class InvertedUserNeighborhood<U> extends UserNeighborhood<U> {

    public InvertedUserNeighborhood(UserNeighborhood<U> neighborhood, Predicate<U> filter) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numUsers(), neighborhood, uidx -> filter.test(neighborhood.uidx2user(uidx))));
    }
    
    public InvertedUserNeighborhood(UserNeighborhood<U> neighborhood, IntPredicate filter) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numUsers(), neighborhood, filter));
    }
}
