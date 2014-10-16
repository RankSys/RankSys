package es.uam.eps.ir.ranksys.core.util.structs;

import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;

public class TopN<T> extends AbstractCollection<T> {

    private final T[] heap;
    private final Comparator<T> cmp;
    private int capacity;
    private int size;

    public TopN(int capacity) {
        this(capacity, (T elem1, T elem2) -> ((Comparable<T>) elem1).compareTo(elem2));
    }

    @SuppressWarnings("unchecked")
    public TopN(int capacity, Comparator<T> cmp) {
        this.capacity = capacity;
        heap = (T[]) new Object[capacity];
        this.cmp = cmp;
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
            while (j != -1 && (cmp.compare(heap[i], heap[j]) > 0)) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }
        }
        size = origSize;
        capacity = origCapacity;
        reverse();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(T elem) {
        if (size < capacity) {
            int i = size;
            int j = parent(i);
            heap[i] = elem;
            while (i > 0 && cmp.compare(heap[j], heap[i]) > 0) {
                swap(i, j);
                i = j;
                j = parent(i);
            }
            size++;

            return true;
        } else {
            if (cmp.compare(heap[0], elem) >= 0) {
                return false;
            }
            int i = 0;
            int j = minChild(i);
            heap[i] = elem;
            while (j != -1 && cmp.compare(heap[i], heap[j]) > 0) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }

            return true;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public T next() {
                return heap[i++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported, ever.");
            }
        };
    }

    public Iterator<T> reverseIterator() {
        return new Iterator<T>() {
            private int i = heap.length - 1;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public T next() {
                return heap[i--];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported, ever.");
            }
        };
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
        T elem = heap[i];
        heap[i] = heap[j];
        heap[j] = elem;
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

        return cmp.compare(heap[l], heap[r]) < 0 ? l : r;
    }

}
