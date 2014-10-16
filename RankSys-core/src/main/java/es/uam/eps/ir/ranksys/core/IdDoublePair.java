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
public class IdDoublePair <I> {

    public I id;
    public double v;

    public IdDoublePair() {
    }

    public IdDoublePair(I id, double v) {
        this.id = id;
        this.v = v;
    }

}
