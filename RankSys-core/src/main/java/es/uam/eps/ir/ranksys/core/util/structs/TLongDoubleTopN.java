package es.uam.eps.ir.ranksys.core.util.structs;

public class TLongDoubleTopN {
    
    private final long[] keys;
    private final double[] values;
    private int capacity;
    private int size;

    public TLongDoubleTopN(int capacity) {
        this.capacity = capacity;
        keys = new long[capacity];
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
            while (j != -1 && (values[i] > values[j] || (values[i] == values[j] && keys[i] > keys[j]))) {
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

    public boolean add(long key, double value) {
        if (size < capacity) {
            int i = size;
            int j = parent(i);
            keys[i] = key;
            values[i] = value;
            while (i > 0 && (values[j] > values[i] || (values[j] == values[i] && keys[j] > keys[i]))) {
                swap(i, j);
                i = j;
                j = parent(i);
            }
            size++;
            
            return true;
        } else {
            if (values[0] > value || (values[0] == value && keys[0] > key)) {
                return false;
            }
            int i = 0;
            int j = minChild(i);
            keys[i] = key;
            values[i] = value;
            while (j != -1 && (values[i] > values[j] || (values[i] == values[j] && keys[i] > keys[j]))) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }
            
            return true;
        }
    }

    public long getKeyAt(int i) {
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
        long k = keys[i];
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

        return (values[l] < values[r] || (values[l] == values[r] && keys[l] < keys[r])) ? l : r;
    }
}
