package org.bok.mk.sukela.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by mk on 17.09.2016.
 */
public class CommonOps {
    public static String getColorString(final int colorResourceCode, final Context ctx) {
        return "#" + Integer.toHexString(ContextCompat.getColor(ctx, colorResourceCode));
    }

    public static void hideSoftKeyboard(View v, Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
