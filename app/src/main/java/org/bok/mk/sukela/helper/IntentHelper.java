package org.bok.mk.sukela.helper;

import android.content.Context;
import android.content.Intent;

/**
 * Created by mk on 24.08.2015.
 */
public class IntentHelper
{
    private IntentHelper()
    {
        throw new IllegalAccessError("You are not welcome here.");
    }

    public static Intent createIntentWithType(Class<?> cls, Meta meta, Context ctx)
    {
        Intent intent = new Intent(ctx, cls);
        intent.putExtra(Contract.META, meta);
        return intent;
    }
}
