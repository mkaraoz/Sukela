package org.bok.mk.sukela.data.model.pack.sakla;

import org.bok.mk.sukela.data.model.Contract;

public class SaklaGoodPack extends SaklaPack
{
    @Override
    public String getTitle() {
        return "Sevdiğim Entriler";
    }

    @Override
    public String getTag() {
        return Contract.TAG_SAVE_FOR_GOOD;
    }
}
