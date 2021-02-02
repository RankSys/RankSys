/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.user;

import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.index.FeatureIndex;
import org.ranksys.core.index.UserIndex;

import java.util.stream.Stream;

/**
 * User-feature data.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 * @param <F> type of the features
 * @param <V> type of the information about user-feature pairs
 */
public interface UserFeatureData<U,F,V> extends UserIndex<U>,FeatureIndex<F>
{
    /**
     * Returns the number of users with features.
     *
     * @return number of users with features
     */
    int numUsersWithFeatures();

    /**
     * Returns the number of features with users.
     *
     * @return number of features with users.
     */
    int numFeaturesWithUsers();

    /**
     * Returns a stream of users with features.
     *
     * @return stream of users with features
     */
    Stream<U> getUsersWithFeatures();

    /**
     * Returns a stream of features with users.
     *
     * @return stream of features with users.
     */
    Stream<F> getFeaturesWithUsers();

    /**
     * Returns a stream of users with the feature.
     *
     * @param f feature
     * @return stream of users with the feature.
     */
    Stream<Tuple2<U, V>> getFeatureUsers(final F f);

    /**
     * Returns a stream of features of the user.
     *
     * @param u user
     * @return stream of features of the user.
     */
    Stream<Tuple2<F, V>> getUserFeatures(final U u);

    /**
     * Returns the number of features of the user.
     *
     * @param u user
     * @return number of features of the user
     */
    int numFeatures(U u);

    /**
     * Returns the number of users with the feature.
     *
     * @param f feature
     * @return number of users with the feature
     */
    int numUsers(F f);
}
