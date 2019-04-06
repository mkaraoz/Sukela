package org.bok.mk.sukela.data.model.pack.uludag;

import org.bok.mk.sukela.data.model.Contract;

public class UludagHebePack extends UludagPack
{
    @Override
    public String getTitle() {
        return "Haftanın en beğenilenleri";
    }

    @Override
    public String getTag() {
        return Contract.TAG_ULUDAG_HAFTA;
    }
}
