/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.diversity.novelty.reranking;

import es.uam.eps.ir.ranksys.diversity.novelty.PCItemNovelty;

/**
 *
 * @author saul
 */
public class PCItemNoveltyReranker<U, I> extends ItemNoveltyReranker<U, I> {

    public PCItemNoveltyReranker(double lambda, PCItemNovelty<U, I> novelty) {
        super(lambda, novelty);
    }
    
    
    public PCItemNoveltyReranker(double lambda, PCItemNovelty<U, I> novelty, int cutoff) {
        super(lambda, novelty, cutoff);
    }
    
}
