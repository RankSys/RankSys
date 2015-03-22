/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Transposed preferences, where users and items change roles. This class is
 * useful to simplify the implementation of many algorithms that work user or
 * item-wise, such as similarities or matrix factorization.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <U> type of the users
 * @param <O> type of other information about preferences
 */
public class TransposedPreferenceData<I, U, O> implements FastPreferenceData<I, U, O> {

    private final FastPreferenceData<U, I, O> d;

    /**
     * Constructor.
     *
     * @param recommenderData preference data to be tranposed
     */
    public TransposedPreferenceData(FastPreferenceData<U, I, O> recommenderData) {
        this.d = recommenderData;
    }

    @Override
    public int user2uidx(I u) {
        return d.item2iidx(u);
    }

    @Override
    public I uidx2user(int uidx) {
        return d.iidx2item(uidx);
    }

    @Override
    public int item2iidx(U i) {
        return d.user2uidx(i);
    }

    @Override
    public U iidx2item(int iidx) {
        return d.uidx2user(iidx);
    }

    @Override
    public int numUsers(int iidx) {
        return d.numItems(iidx);
    }

    @Override
    public int numItems(int uidx) {
        return d.numUsers(uidx);
    }

    @Override
    public Stream<IdxPref<O>> getUidxPreferences(int uidx) {
        return d.getIidxPreferences(uidx);
    }

    @Override
    public Stream<IdxPref<O>> getIidxPreferences(int iidx) {
        return d.getUidxPreferences(iidx);
    }

    @Override
    public int numUsers() {
        return d.numItems();
    }

    @Override
    public int numUsers(U i) {
        return d.numItems(i);
    }

    @Override
    public int numItems() {
        return d.numUsers();
    }

    @Override
    public int numItems(I u) {
        return d.numUsers(u);
    }

    @Override
    public int numPreferences() {
        return d.numPreferences();
    }

    @Override
    public Stream<I> getAllUsers() {
        return d.getAllItems();
    }

    @Override
    public Stream<U> getAllItems() {
        return d.getAllUsers();
    }

    @Override
    public Stream<IdPref<U, O>> getUserPreferences(I u) {
        return d.getItemPreferences(u);
    }

    @Override
    public Stream<IdPref<I, O>> getItemPreferences(U i) {
        return d.getUserPreferences(i);
    }

    @Override
    public IntStream getAllUidx() {
        return d.getAllIidx();
    }

    @Override
    public IntStream getAllIidx() {
        return d.getAllUidx();
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return d.getIidxWithPreferences();
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return d.getUidxWithPreferences();
    }

    @Override
    public int numUsersWithPreferences() {
        return d.numItemsWithPreferences();
    }

    @Override
    public int numItemsWithPreferences() {
        return d.numUsersWithPreferences();
    }

    @Override
    public Stream<I> getUsersWithPreferences() {
        return d.getItemsWithPreferences();
    }

    @Override
    public Stream<U> getItemsWithPreferences() {
        return d.getUsersWithPreferences();
    }
}
