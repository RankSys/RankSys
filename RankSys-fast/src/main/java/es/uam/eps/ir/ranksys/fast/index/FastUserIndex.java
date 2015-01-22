/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.core.index.UserIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface FastUserIndex<U> extends UserIndex<U> {

    @Override
    public default boolean containsUser(U u) {
        return user2uidx(u) >= 0;
    }

    @Override
    public default Stream<U> getAllUsers() {
        return IntStream.range(0, numUsers()).mapToObj(uidx -> uidx2user(uidx));
    }

    public int user2uidx(U u);

    public U uidx2user(int uidx);

}
