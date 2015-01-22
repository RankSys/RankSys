/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast;

/**
 *
 * @author saul
 */
public class IdxVar<V> {

    public int idx;
    public V v;

    public IdxVar() {
    }

    public IdxVar(int idx, V v) {
        this.idx = idx;
        this.v = v;
    }

}
