/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.lda;

import cc.mallet.pipe.Noop;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.io.IOException;
import java.util.Iterator;
import static java.util.stream.IntStream.range;

/**
 * LDA model estimator. See ParallelTopicModel in Mallet (http://mallet.cs.umass.edu/) for more details.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class LDAModelEstimator {

    /**
     * Estimate a topic model for collaborative filtering data.
     *
     * @param <U> user type
     * @param <I> item type
     * @param preferences preference data
     * @param k number of topics
     * @param alpha alpha in model
     * @param beta beta in model
     * @param numIterations number of iterations
     * @param burninPeriod burnin period
     * @return a topic model
     * @throws IOException when internal IO error occurs
     */
    public static <U, I> ParallelTopicModel estimate(FastPreferenceData<U, I> preferences, int k, double alpha, double beta, int numIterations, int burninPeriod) throws IOException {
        
        ParallelTopicModel topicModel = new ParallelTopicModel(k, alpha * k, beta);
        topicModel.addInstances(new LDAInstanceList<>(preferences));
        topicModel.setTopicDisplay(numIterations + 1, 0);
        topicModel.setNumIterations(numIterations);
        topicModel.setBurninPeriod(burninPeriod);
        topicModel.setNumThreads(Runtime.getRuntime().availableProcessors());

        topicModel.estimate();

        return topicModel;
    }

    private static class LDAAlphabet extends Alphabet {

        private final int numItems;

        public LDAAlphabet(int numItems) {
            this.numItems = numItems;
        }

        @Override
        public int size() {
            return numItems;
        }

    }

    private static class LDAInstanceList<U, I> extends InstanceList {

        private final FastPreferenceData<U, I> preferences;
        private final Alphabet alphabet;

        public LDAInstanceList(FastPreferenceData<U, I> preferences) {
            super(new Noop());
            this.preferences = preferences;
            this.alphabet = new LDAAlphabet(preferences.numItems());
        }

        @Override
        public Iterator<Instance> iterator() {
            Iterator<Instance> iterator = preferences.getAllUidx()
                    .mapToObj(preferences::getUidxPreferences)
                    .map(userPreferences -> {
                        FeatureSequence sequence = new FeatureSequence(alphabet);
                        userPreferences.forEach(pref -> {
                            range(0, (int) pref.v2).forEach(i -> sequence.add(pref.v1));
                        });

                        return new Instance(sequence, null, null, null);
                    })
                    .iterator();

            return iterator;
        }

        @Override
        public Alphabet getDataAlphabet() {
            return alphabet;
        }

    }
}
