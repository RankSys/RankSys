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
public interface IndexedItem<U> {

    public IdxMapper<U> getUserMapper();

    public int user2uidx(U u);

    public U uidx2user(int uidx);

}
