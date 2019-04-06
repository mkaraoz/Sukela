package org.bok.mk.sukela.data.model.pack.eksi;

import org.bok.mk.sukela.data.model.Contract;

public class EksiHebePack extends EksiPack
{
    @Override
    public String getTitle() {
        return "Haftanın en beğenilenleri";
    }

    @Override
    public String getTag() {
        return Contract.TAG_EKSI_WEEK;
    }
}
