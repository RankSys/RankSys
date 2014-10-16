/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

/**
 *
 * @author saul
 */
public class Parsers {

    public static IntegerParser ip = new IntegerParser();
    public static LongParser lp = new LongParser();
    public static Parser<String> sp = from -> from.toString();
    public static FloatParser fp = new FloatParser();
    public static DoubleParser dp = new DoubleParser();
    public static Parser<Void> vp = from -> null;
}
