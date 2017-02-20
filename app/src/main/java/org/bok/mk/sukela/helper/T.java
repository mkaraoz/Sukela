package org.bok.mk.sukela.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by mk on 18.12.2016.
 */

public final class T
{
    public static final void toast(final Context ctx, final String message)
    {
        makeToast(ctx, message, Toast.LENGTH_SHORT);
    }

    public static final void toastLong(final Context ctx, final String message)
    {
        makeToast(ctx, message, Toast.LENGTH_LONG);
    }

    private static final void makeToast(final Context ctx, final String message, final int duration)
    {
        Toast.makeText(ctx, message, duration).show();
    }
}
