package org.bok.mk.sukela.ui.year;

import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.eksi.EksiYearPack;

interface YearContract
{
    interface View
    {
        void startEntryScreen(final Pack pack);
    }

    interface Presenter
    {
        void onYearListClick(final EksiYearPack.Year year);
    }
}
