/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.tools;

import static java.lang.Class.forName;
import static java.util.Arrays.copyOfRange;

/**
 *
 * @author saul
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String main = "es.uam.eps.ir.rs2.oair2013." + args[0];
        args = copyOfRange(args, 1, args.length);

        Class[] argTypes = {args.getClass(),};
        Object[] passedArgs = {args};
        forName(main).getMethod("main", argTypes).invoke(null, passedArgs);
    }

}
