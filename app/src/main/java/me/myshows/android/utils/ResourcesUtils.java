package me.myshows.android.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by warrior on 24.10.15.
 */
public class ResourcesUtils {

    private ResourcesUtils() {
    }

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id);
        }
        return context.getDrawable(id);
    }

    @ColorInt
    public static int getColor(@NonNull Context context, @ColorRes int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.getResources().getColor(id);
        }
        return context.getColor(id);
    }

    @Nullable
    public static ColorStateList getColorStateList(@NonNull Context context, @ColorRes int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.getResources().getColorStateList(id);
        }
        return context.getColorStateList(id);
    }
}
