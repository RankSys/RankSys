/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.index.MutableItemIndex;
import es.uam.eps.ir.ranksys.core.index.MutableUserIndex;

/**
 *
 * @author saul
 */
public interface MutablePreferenceData<U, I> extends PreferenceData<U, I>, MutableUserIndex<U>, MutableItemIndex<I> {

    public boolean addPref(U u, IdPref<I> pref);
    
    public boolean removePref(U u, I i);
}
