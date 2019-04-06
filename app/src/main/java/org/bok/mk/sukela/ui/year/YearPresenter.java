package org.bok.mk.sukela.ui.year;

import org.bok.mk.sukela.data.model.pack.Pack;
import org.bok.mk.sukela.data.model.pack.eksi.EksiYearPack;
import org.bok.mk.sukela.ui.base.BasePresenter;

public class YearPresenter extends BasePresenter<YearContract.View> implements YearContract.Presenter
{
    @Override
    public void onYearListClick(EksiYearPack.Year year) {
        Pack pack = new EksiYearPack(year);
        mView.startEntryScreen(pack);
    }
}
