/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.novelty.metrics;

import es.uam.eps.ir.ranksys.diversity.novelty.PCItemNovelty;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;

/**
 *
 * @author saul
 */
public class EPC<U, I> extends ItemNoveltyMetric<U, I> {

    public EPC(int cutoff, PCItemNovelty<U, I> novelty, RelevanceModel<U, I> relModel) {
        super(cutoff, novelty, relModel);
    }

}
