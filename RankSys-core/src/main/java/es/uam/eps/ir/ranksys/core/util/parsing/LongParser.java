/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

/**
 *
 * @author saul
 */
public class LongParser implements Parser<Long> {

    @Override
    public Long parse(CharSequence seq) {
        long val = 0;

        for (int i = 0; i < seq.length(); i++) {
            val = (seq.charAt(i) - '0') + val * 10;
        }

        return val;
    }
}
