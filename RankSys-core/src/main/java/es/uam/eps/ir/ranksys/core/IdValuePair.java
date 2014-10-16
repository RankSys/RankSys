/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core;

/**
 *
 * @author saul
 */
public class IdValuePair<I, V> {

    public I id;
    public V v;

    public IdValuePair() {
    }

    public IdValuePair(I id, V v) {
        this.id = id;
        this.v = v;
    }

}
