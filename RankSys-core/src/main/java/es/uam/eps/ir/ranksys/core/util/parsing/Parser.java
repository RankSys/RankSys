/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

/**
 *
 * @author saul
 */
public interface Parser<T> {

    public T parse(CharSequence from);
}
