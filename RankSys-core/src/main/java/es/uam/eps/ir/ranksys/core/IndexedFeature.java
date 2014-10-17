/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.core;

import es.uam.eps.ir.ranksys.core.util.IdxMapper;

/**
 *
 * @author saul
 */
public interface IndexedFeature<F> {

    public IdxMapper<F> getFeatureMapper();

    public int feature2fidx(F f);

    public F fidx2feature(int fidx);

}
