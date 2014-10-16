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
public class IdxValuePair<V> {

    public int idx;
    public V v;

    public IdxValuePair() {
    }

    public IdxValuePair(int idx, V v) {
        this.idx = idx;
        this.v = v;
    }

}
