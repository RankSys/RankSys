/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.index;

/**
 *
 * @author saul
 */
public interface MutableItemIndex<I> extends ItemIndex<I> {

    public I addItem();

    public boolean addItem(I i);

    public boolean removeItem(I i);
}
