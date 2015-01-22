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
public class IdxPref<O> {

    public final int idx;
    public final double v;
    public final O o;

    public IdxPref(int idx, double value, O other) {
        this.idx = idx;
        this.v = value;
        this.o = other;
    }

}
