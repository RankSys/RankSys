package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdValuePair;

public interface RecommenderData<U, I, V> {

    public int numUsers();

    public int numUsers(I i);

    public int numItems();

    public int numItems(U u);

    public int numPreferences();

    public Iterable<U> getAllUsers();

    public Iterable<I> getAllItems();

    public Iterable<IdValuePair<I, V>> getUserPreferences(U u);

    public Iterable<IdValuePair<U, V>> getItemPreferences(I i);

    public void addUser(U user);
    
    public void addItem(I item);
    
    public void addUserPreferences(U user, Iterable<IdValuePair<I, V>> userPreferences);
    
    public void addItemPreferences(I item, Iterable<IdValuePair<U, V>> itemPreferences);
}
