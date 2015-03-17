/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.nn.neighborhood.InvertedNeighborhood;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class InvertedItemNeighborhood<I> extends ItemNeighborhood<I> {

    public InvertedItemNeighborhood(ItemNeighborhood<I> neighborhood) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numItems(), neighborhood, iidx -> true));
    }
    
}
