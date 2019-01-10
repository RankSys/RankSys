/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.core.util.unties;

import java.util.Comparator;

/**
 * Basic untie element which uses 
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> The type of the element.
 */
public class IdentifierUntiePolicy<T> implements UntiePolicy<T> 
{
    @Override
    public Comparator<T> comparator()
    {
        return (t1,t2) -> ((Comparable<T>) t1).compareTo(t2);
    }

    @Override
    public void update()
    {
        
    }
}
