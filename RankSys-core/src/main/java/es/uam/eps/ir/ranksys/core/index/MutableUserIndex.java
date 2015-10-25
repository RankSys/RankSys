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
public interface MutableUserIndex<U> extends UserIndex<U> {

    public U addUser();

    public boolean addUser(U u);

    public boolean removeUser(U u);
}
