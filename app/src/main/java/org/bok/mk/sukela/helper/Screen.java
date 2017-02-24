package org.bok.mk.sukela.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by mk on 04.09.2016.
 */
public class Screen {
    public static String getSize(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "xlarge";
            default:
                return "undefined";
        }
    }

    public static boolean isXLScreen(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static float getWidthDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return dpWidth;
    }

    public static float getWidthPx(Context context) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getWidthDp(context), context.getResources().getDisplayMetrics());
        return px;
    }

    public static float getHeightDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        return dpHeight;
    }

    public static float getHeightPx(Context context) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getHeightDp(context), context.getResources().getDisplayMetrics());
        return px;
    }
}
