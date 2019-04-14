/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ranksys.core.util.unties;

import java.util.Comparator;

/**
 * Class for deciding which key is better in case of tie.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> the object to compare.
 */
public interface UntiePolicy<T>
{
    /**
     * Obtains a comparator
     * @return a comparator.
     */
    public Comparator<T> comparator();
    
    /**
     * Updates the untie policy.
     */
    public void update();
}
