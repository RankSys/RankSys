/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class SimpleFastUserIndex<U> implements FastUserIndex<U> {

    private final IdxIndex<U> uMap;

    public SimpleFastUserIndex() {
        this.uMap = new IdxIndex<>();
    }

    @Override
    public boolean containsUser(U u) {
        return uMap.containsId(u);
    }

    @Override
    public int numUsers() {
        return uMap.size();
    }

    @Override
    public Stream<U> getAllUsers() {
        return StreamSupport.stream(uMap.getIds().spliterator(), false);
    }

    @Override
    public int user2uidx(U u) {
        return uMap.get(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uMap.get(uidx);
    }

    public int add(U u) {
        return uMap.add(u);
    }

}
