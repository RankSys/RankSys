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
public interface IndexedUser<I> {

    public IdxMapper<I> getItemMapper();

    public int item2iidx(I i);

    public I iidx2item(int iidx);

}
