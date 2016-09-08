/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jooq.lambda.function.Function2;
import org.ranksys.fast.preference.FastPointWisePreferenceData;

/**
 * Transposed preferences, where users and items change roles. This class is useful to simplify the implementation of many algorithms that work user or item-wise, such as similarities or matrix factorization.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 * @param <U> type of the users
 */
public class TransposedPreferenceData<I, U> implements FastPreferenceData<I, U>, FastPointWisePreferenceData<I, U> {

    private final FastPreferenceData<U, I> d;
    private final Function2<U, IdPref<I>, IdPref<U>> idPrefFun;
    private final Function2<Integer, IdxPref, IdxPref> idxPrefFun;

    /**
     * Constructor with default converters between IdxPref and IdPref.
     *
     * @param recommenderData preference data to be transposed
     */
    public TransposedPreferenceData(FastPreferenceData<U, I> recommenderData) {
        this(recommenderData, (u, p) -> new IdPref<>(u, p.v2), (uidx, p) -> new IdxPref(uidx, p.v2));
    }

    /**
     * Constructor with custom converters between IdxPref and IdPref.
     *
     * @param recommenderData preference data to be transposed
     * @param idPrefFun converter from item IdPref to user IdPref
     * @param idxPrefFun converter from item IdxPref to item IdxPref
     */
    public TransposedPreferenceData(FastPreferenceData<U, I> recommenderData,
                                    Function2<U, IdPref<I>, IdPref<U>> idPrefFun,
                                    Function2<Integer, IdxPref, IdxPref> idxPrefFun) {
        this.d = recommenderData;
        this.idPrefFun = idPrefFun;
        this.idxPrefFun = idxPrefFun;
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
    public Stream<? extends IdxPref> getUidxPreferences(int uidx) {
        return d.getIidxPreferences(uidx);
    }

    @Override
    public Stream<? extends IdxPref> getIidxPreferences(int iidx) {
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
    public Stream<? extends IdPref<U>> getUserPreferences(I u) {
        return d.getItemPreferences(u);
    }

    @Override
    public Stream<? extends IdPref<I>> getItemPreferences(U i) {
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

    @Override
    public IntIterator getUidxIidxs(int uidx) {
        return d.getIidxUidxs(uidx);
    }

    @Override
    public DoubleIterator getUidxVs(int uidx) {
        return d.getIidxVs(uidx);
    }

    @Override
    public IntIterator getIidxUidxs(int iidx) {
        return d.getUidxIidxs(iidx);
    }

    @Override
    public DoubleIterator getIidxVs(int iidx) {
        return d.getUidxVs(iidx);
    }

    @Override
    public Optional<IdxPref> getPreference(int uidx, int iidx) {
        Optional<? extends IdxPref> pref = ((FastPointWisePreferenceData<U, I>) d).getPreference(iidx, uidx);
        if (pref.isPresent()) {
            return Optional.of(idxPrefFun.apply(iidx, pref.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<IdPref<U>> getPreference(I u, U i) {
        Optional<? extends IdPref<I>> pref = ((FastPointWisePreferenceData<U, I>) d).getPreference(i, u);
        if (pref.isPresent()) {
            return Optional.of(idPrefFun.apply(i, pref.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return d.useIteratorsPreferentially();
    }

}
