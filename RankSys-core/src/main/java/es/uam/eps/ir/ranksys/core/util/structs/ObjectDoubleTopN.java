/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.core.util.structs;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class ObjectDoubleTopN<T> {

    private final T[] keys;
    private final double[] values;
    private int capacity;
    private int size;

    public ObjectDoubleTopN(int capacity) {
        this.capacity = capacity;
        keys = (T[]) new Object[capacity];
        values = new double[capacity];
        size = 0;
    }

    public void sort() {
        int origSize = size;
        int origCapacity = capacity;
        capacity = size;
        while (size > 0) {
            swap(0, size - 1);
            size--;
            capacity--;
            int i = 0;
            int j = minChild(i);
            while (j != -1 && (values[i] > values[j] || (values[i] == values[j] && ((Comparable<T>) keys[i]).compareTo(keys[j]) > 0))) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }
        }
        size = origSize;
        capacity = origCapacity;
        reverse();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean add(T key, double value) {
        if (size < capacity) {
            int i = size;
            int j = parent(i);
            keys[i] = key;
            values[i] = value;
            while (i > 0 && (values[j] > values[i] || (values[j] == values[i] && ((Comparable<T>) keys[j]).compareTo(keys[i]) > 0))) {
                swap(i, j);
                i = j;
                j = parent(i);
            }
            size++;

            return true;
        } else {
            if (values[0] > value || (values[0] == value && ((Comparable<T>) keys[0]).compareTo(key) > 0)) {
                return false;
            }
            int i = 0;
            int j = minChild(i);
            keys[i] = key;
            values[i] = value;
            while (j != -1 && (values[i] > values[j] || (values[i] == values[j] && ((Comparable<T>) keys[i]).compareTo(keys[j]) > 0))) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }

            return true;
        }
    }

    public T getKeyAt(int i) {
        return keys[i];
    }

    public double getValueAt(int i) {
        return values[i];
    }

    private void reverse() {
        int i = 0;
        int j = size - 1;
        while (j > i) {
            swap(i, j);
            j--;
            i++;
        }
    }

    private void swap(int i, int j) {
        T k = keys[i];
        keys[i] = keys[j];
        keys[j] = k;
        double v = values[i];
        values[i] = values[j];
        values[j] = v;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return (i + 1) * 2 - 1;
    }

    private int right(int i) {
        return (i + 1) * 2;
    }

    private int minChild(int i) {
        int l = left(i);
        int r = right(i);

        if (l >= capacity) {
            return -1;
        }

        if (r >= size) {
            return l;
        }

        return (values[l] < values[r] || (values[l] == values[r] && ((Comparable<T>) keys[l]).compareTo(keys[r]) < 0)) ? l : r;
    }
}
