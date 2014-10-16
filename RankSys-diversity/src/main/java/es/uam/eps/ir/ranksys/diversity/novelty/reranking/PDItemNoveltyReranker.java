/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.diversity.novelty.reranking;

import es.uam.eps.ir.ranksys.diversity.novelty.PDItemNovelty;

/**
 *
 * @author saul
 */
public class PDItemNoveltyReranker<U, I> extends ItemNoveltyReranker<U, I> {

    public PDItemNoveltyReranker(double lambda, PDItemNovelty<U, I> novelty) {
        super(lambda, novelty);
    }
    
    public PDItemNoveltyReranker(double lambda, PDItemNovelty<U, I> novelty, int cutoff) {
        super(lambda, novelty, cutoff);
    }
    
}
