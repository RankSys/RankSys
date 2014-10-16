/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

import static java.lang.Double.parseDouble;

/**
 *
 * @author saul
 */
public class DoubleParser implements Parser<Double> {

    @Override
    public Double parse(CharSequence from) {
        return parseDouble(from.toString());
    }
    
}
