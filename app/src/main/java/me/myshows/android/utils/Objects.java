package me.myshows.android.utils;

/**
 * Created by warrior on 07.02.16.
 */
public final class Objects {

    private Objects() {
    }

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}.
     */
    public static <T> T requireNonNull(T o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return o;
    }

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}
     * with the given detail message.
     */
    public static <T> T requireNonNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }
}
