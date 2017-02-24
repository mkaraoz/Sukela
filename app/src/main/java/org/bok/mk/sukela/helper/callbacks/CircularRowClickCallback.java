package org.bok.mk.sukela.helper.callbacks;

/**
 * Created by mk on 18.09.2016.
 */
public interface CircularRowClickCallback {
    void rowClicked(final String tag, final String data);

    void rowLongClicked(final String tag, final String data);
}
