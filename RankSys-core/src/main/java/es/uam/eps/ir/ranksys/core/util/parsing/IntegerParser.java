/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

/**
 *
 * @author saul
 */
public class IntegerParser implements Parser<Integer> {

    @Override
    public Integer parse(CharSequence seq) {
        int val = 0;

        for (int i = 0; i < seq.length(); i++) {
            val = (seq.charAt(i) - '0') + val * 10;
        }

        return val;
    }
}
