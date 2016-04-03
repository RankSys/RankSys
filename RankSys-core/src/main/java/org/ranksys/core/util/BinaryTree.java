/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util;

import static java.lang.Math.max;
import org.ranksys.core.util.BinaryTree.Node;

/**
 * Binary tree.
 *
 * A simple binary tree for different purposes.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public class BinaryTree<N extends Node> {

    private final N rootNode;

    /**
     * Constructor.
     *
     * @param root root node
     */
    public BinaryTree(N root) {
        rootNode = root;
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * @return number of nodes
     */
    public int numNodes() {
        return rootNode.numNodes();
    }

    /**
     * Returns the depth of the tree.
     *
     * @return depth of the tree
     */
    public int depth() {
        return rootNode.depth();
    }

    /**
     * Returns the root node.
     *
     * @return the root node
     */
    public N getRoot() {
        return rootNode;
    }

    /**
     * Node of a binary tree.
     */
    public static abstract class Node {

        private Node p;
        private Node l;
        private Node r;

        /**
         * Constructor.
         */
        public Node() {
            p = null;
            l = null;
            r = null;
        }

        /**
         * Returns the parent node (if any) of this node.
         *
         * @return parent node
         */
        public Node getParent() {
            return p;
        }

        /**
         * Returns the left child of this node.
         *
         * @return left child
         */
        public Node getLeftChild() {
            return l;
        }

        /**
         * Sets a node as left child of this node.
         *
         * @param leftChild left child
         */
        public void setLeftChild(Node leftChild) {
            this.l = leftChild;
            if (leftChild != null) {
                this.l.p = this;
            }
        }

        /**
         * Returns the right child of this node.
         *
         * @return right child
         */
        public Node getRightChild() {
            return r;
        }

        /**
         * Sets a node as right child of this node.
         *
         * @param rightChild right node
         */
        public void setRightChild(Node rightChild) {
            this.r = rightChild;
            if (rightChild != null) {
                this.r.p = this;
            }
        }

        /**
         * Determines whether the node is a leaf in the tree.
         *
         * @return true if it is leaf, false otherwise
         */
        public boolean isLeaf() {
            return l == null && r == null;
        }

        protected int numNodes() {
            int c = 1;
            if (this.l != null) {
                c += l.numNodes();
            }
            if (this.r != null) {
                c += r.numNodes();
            }

            return c;
        }

        protected int depth() {
            if (isLeaf()) {
                return 1;
            } else {
                return 1 + max(l.depth(), r.depth());
            }
        }

    }
}
