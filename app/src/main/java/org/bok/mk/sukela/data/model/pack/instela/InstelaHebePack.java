package org.bok.mk.sukela.data.model.pack.instela;

import org.bok.mk.sukela.data.model.Contract;

public class InstelaHebePack extends InstelaPack
{
    @Override
    public String getTitle() {
        return "Haftanın en beğenilenleri";
    }

    @Override
    public String getTag() {
        return Contract.TAG_INSTELA_HAFTA;
    }
}
