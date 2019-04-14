/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.core.util.unties.fast;

import java.util.Comparator;
import org.ranksys.core.index.fast.FastItemIndex;

/**
 * Basic untie element which uses the identifiers of the elements
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> The type of the element.
 */
public class IdentifierUntiePolicy<T> extends AbstractFastUntiePolicy<T> 
{
    public IdentifierUntiePolicy(FastItemIndex<T> index)
    {
        super(index);
    }
    
    @Override
    public Comparator<T> comparator()
    {
        return (t1,t2) -> ((Comparable<T>) t1).compareTo(t2);
    }

    @Override
    public void update()
    {
        
    }

    @Override
    public Comparator<Integer> fastComparator()
    {
        return (x,y) -> ((Comparable<T>) index.iidx2item(x)).compareTo(index.iidx2item(y));
    }
}
