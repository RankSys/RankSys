/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.diversity.novelty.reranking;

import es.uam.eps.ir.ranksys.diversity.novelty.FDItemNovelty;

/**
 *
 * @author saul
 */
public class FDItemNoveltyReranker<U, I> extends ItemNoveltyReranker<U, I> {

    public FDItemNoveltyReranker(double lambda, FDItemNovelty<U, I> novelty) {
        super(lambda, novelty);
    }
    
    public FDItemNoveltyReranker(double lambda, FDItemNovelty<U, I> novelty, int cutoff) {
        super(lambda, novelty, cutoff);
    }
    
}
