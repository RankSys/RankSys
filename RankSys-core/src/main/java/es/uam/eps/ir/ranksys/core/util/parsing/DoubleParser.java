/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

/**
 *
 * @author saul
 */
public interface DoubleParser {

    public double parse(CharSequence from);
    
    public static final DoubleParser ddp = (token) -> Double.parseDouble(token.toString());
}
