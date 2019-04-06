package org.bok.mk.sukela.data.model.pack.instela;

import org.bok.mk.sukela.data.model.Contract;

public class InstelaTopPack extends InstelaPack
{
    @Override
    public String getTitle() {
        return "Top 20";
    }

    @Override
    public String getTag() {
        return Contract.TAG_INSTELA_TOP_20;
    }
}
