package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import java.util.stream.Stream;

public interface RecommenderData<U, I, V> {

    public int numUsers();

    public int numUsers(I i);

    public int numItems();

    public int numItems(U u);

    public int numPreferences();

    public Stream<U> getAllUsers();

    public Stream<I> getAllItems();

    public Stream<IdValuePair<I, V>> getUserPreferences(U u);

    public Stream<IdValuePair<U, V>> getItemPreferences(I i);
}
