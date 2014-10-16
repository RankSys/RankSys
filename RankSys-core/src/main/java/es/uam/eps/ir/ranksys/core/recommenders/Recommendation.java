package es.uam.eps.ir.ranksys.core.recommenders;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import java.util.List;

/**
 *
 * @author saul
 */
public class Recommendation<U, I> {

    private final U user;
    private final List<IdDoublePair<I>> items;

    public Recommendation(U user, List<IdDoublePair<I>> items) {
        this.user = user;
        this.items = items;
    }

    public U getUser() {
        return user;
    }

    public List<IdDoublePair<I>> getItems() {
        return items;
    }
}
