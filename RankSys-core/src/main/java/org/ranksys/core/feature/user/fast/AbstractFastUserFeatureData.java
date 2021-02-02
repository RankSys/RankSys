/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.user.fast;

import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.util.tuples.Tuple2io;

import java.util.stream.Stream;

/**
 * Abstract FastUserFeatureData, implementing the interfaces of FastUserIndex and
 * FastFeatureIndex by delegating to implementations of these.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 * @param <F> type of the features
 * @param <V> type of the information about user-feature pairs
 */
public abstract class AbstractFastUserFeatureData<U,F,V> implements FastUserFeatureData<U,F,V>
{
    private final FastUserIndex<U> ui;
    private final FastFeatureIndex<F> fi;

    /**
     * Constructor.
     *
     * @param ui user index
     * @param fi feature index
     */
    protected AbstractFastUserFeatureData(FastUserIndex<U> ui, FastFeatureIndex<F> fi) {
        this.ui = ui;
        this.fi = fi;
    }
    @Override
    public Stream<U> getUsersWithFeatures() {
        return getUidxWithFeatures().mapToObj(this::uidx2user);
    }

    @Override
    public Stream<F> getFeaturesWithUsers() {
        return getFidxWithUsers().mapToObj(this::fidx2feature);
    }

    @Override
    public int numFeatures(U u) {
        return numFeatures(user2uidx(u));
    }

    @Override
    public int numUsers(F f) {
        return numUsers(feature2fidx(f));
    }

    @Override
    public Stream<Tuple2<U, V>> getFeatureUsers(F f) {
        return getFidxUsers(feature2fidx(f)).map(this::uidx2user);
    }

    @Override
    public Stream<Tuple2<F, V>> getUserFeatures(U u) {
        return getUidxFeatures(user2uidx(u)).map(this::fidx2feature);
    }

    @Override
    public boolean containsUser(U u) {
        return ui.containsUser(u);
    }

    @Override
    public int numUsers() {
        return ui.numUsers();
    }

    @Override
    public Stream<U> getAllUsers() {
        return ui.getAllUsers();
    }

    @Override
    public boolean containsFeature(F f) {
        return fi.containsFeature(f);
    }

    @Override
    public int numFeatures() {
        return fi.numFeatures();
    }

    @Override
    public Stream<F> getAllFeatures() {
        return fi.getAllFeatures();
    }

    @Override
    public int user2uidx(U i) {
        return ui.user2uidx(i);
    }

    @Override
    public U uidx2user(int uidx) {
        return ui.uidx2user(uidx);
    }

    @Override
    public int feature2fidx(F f) {
        return fi.feature2fidx(f);
    }

    @Override
    public F fidx2feature(int fidx) {
        return fi.fidx2feature(fidx);
    }

    @Override
    public <V> Tuple2<F, V> fidx2feature(Tuple2io<V> tuple)
    {
        return fi.fidx2feature(tuple);
    }

    @Override
    public <V> Tuple2<U, V> uidx2user(Tuple2io<V> tuple)
    {
        return ui.uidx2user(tuple);
    }
}
