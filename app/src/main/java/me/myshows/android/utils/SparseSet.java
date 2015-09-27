package me.myshows.android.utils;

import android.util.SparseArray;

/**
 * Created by warrior on 27.08.15.
 */
public class SparseSet {

    private static final Object PRESENT = new Object();

    private final SparseArray<Object> array;

    public SparseSet() {
        array = new SparseArray<>();
    }

    public SparseSet(int initialCapacity) {
        array = new SparseArray<>(initialCapacity);
    }

    public void add(int value) {
        array.put(value, PRESENT);
    }

    public boolean contains(int value) {
        return array.get(value) != null;
    }

    public void remove(int value) {
        array.remove(value);
    }

    public int valueAt(int index) {
        return array.keyAt(index);
    }

    public int size() {
        return array.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        array.clear();
    }
}
