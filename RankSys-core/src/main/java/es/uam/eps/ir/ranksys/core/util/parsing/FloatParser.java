/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

import static java.lang.Float.parseFloat;

/**
 *
 * @author saul
 */
public class FloatParser implements Parser<Float> {

    @Override
    public Float parse(CharSequence from) {
        return parseFloat(from.toString());
    }
    
}
