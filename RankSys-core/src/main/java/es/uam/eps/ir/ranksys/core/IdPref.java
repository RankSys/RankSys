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
public class IdPref<I, O> {

    public final I id;
    public final double v;
    public final O o;

    public IdPref(I id, double value, O other) {
        this.id = id;
        this.v = value;
        this.o = other;
    }

}
