/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.sampling;

import org.ranksys.core.util.BinaryTree;
import org.ranksys.core.util.BinaryTree.Node;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

/**
 * Weighted sampling with and without replacement.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <T> type of sampled objects
 */
public class WeightedSampling<T> {

    private final Random rnd;
    private final BinaryTree tree;
    private final boolean withReplacement;
    private int n;

    /**
     * Constructor
     *
     * @param elems stream of element to sample from
     * @param value sampling weight function
     * @param withReplacement if true selection with replacement, without otherwise
     */
    public WeightedSampling(Stream<T> elems, ToDoubleFunction<T> value, boolean withReplacement) {
        this(elems, value, withReplacement, new Random());
    }

    /**
     * Constructor.
     *
     * @param elems stream of element to sample from
     * @param value sampling weight function
     * @param withReplacement if true selection with replacement, without otherwise
     * @param seed seed to Random constructor
     */
    public WeightedSampling(Stream<T> elems, ToDoubleFunction<T> value, boolean withReplacement, long seed) {
        this(elems, value, withReplacement, new Random(seed));
    }

    /**
     * Constructor.
     *
     * @param elems stream of element to sample from
     * @param value sampling weight function
     * @param withReplacement if true selection with replacement, without otherwise
     * @param rnd random
     */
    public WeightedSampling(Stream<T> elems, ToDoubleFunction<T> value, boolean withReplacement, Random rnd) {
        this.rnd = rnd;
        this.withReplacement = withReplacement;

        Queue<EVNode<T>> queue = new LinkedList<>();
        elems.forEach(elem -> {
            EVNode node = new EVNode(elem, value.applyAsDouble(elem));
            queue.add(node);
        });
        this.n = queue.size();

        while (queue.size() > 1) {
            EVNode l = queue.poll();
            EVNode r = queue.poll();
            EVNode p = new EVNode(null, l.value + r.value);
            p.setLeftChild(l);
            p.setRightChild(r);
            queue.add(p);
        }

        EVNode<T> root = queue.poll();
        this.tree = new BinaryTree(root);
    }

    /**
     * Sample k elements.
     *
     * @param k (maximum) number of element to sample
     * @return sampled elements
     */
    public Stream<T> sample(int k) {
        if (!withReplacement) {
            k = min(k, n);
        }

        List<T> sample = new ArrayList<>();
        for (int t = 0; t < k; t++) {
            sample.add(sample());
        }

        return sample.stream();
    }

    /**
     * Sample an element.
     *
     * @return sampled element
     */
    public T sample() {
        double v = rnd.nextDouble() * ((EVNode<T>) tree.getRoot()).value;
        EVNode<T> node = binSearch(v);

        if (!withReplacement) {
            double value = node.value;
            EVNode<T> parent = (EVNode<T>) node.getParent();
            if (parent.getLeftChild() == node) {
                parent.setLeftChild(null);
            } else {
                parent.setRightChild(null);
            }
            while (parent != null) {
                parent.value -= value;
                parent = (EVNode<T>) parent.getParent();
            }
            n--;
        }

        return node.elem;
    }

    private EVNode<T> binSearch(double v) {
        return binSearch((EVNode<T>) tree.getRoot(), v);
    }

    private EVNode<T> binSearch(EVNode<T> node, double v) {
        EVNode<T> l = (EVNode<T>) node.getLeftChild();
        EVNode<T> r = (EVNode<T>) node.getRightChild();
        if (l == null && r == null) {
            return node;
        } else if (l == null) {
            return binSearch(r, v);
        } else if (r == null) {
            return binSearch(l, v);
        } else {
            if (v <= l.value) {
                return binSearch(l, v);
            } else {
                return binSearch(r, v - l.value);
            }
        }
    }

    private class EVNode<T> extends Node {

        private final T elem;
        private double value;

        public EVNode(T elem, double value) {
            this.elem = elem;
            this.value = value;
        }

    }
}
