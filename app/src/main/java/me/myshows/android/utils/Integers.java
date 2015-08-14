package me.myshows.android.utils;

/**
 * Created by warrior on 13.08.15.
 */
public class Integers {

    private Integers() {}

    public static int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
