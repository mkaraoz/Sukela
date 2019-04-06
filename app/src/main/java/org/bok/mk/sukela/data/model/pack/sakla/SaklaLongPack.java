package org.bok.mk.sukela.data.model.pack.sakla;

import org.bok.mk.sukela.data.model.Contract;

public class SaklaLongPack extends SaklaPack
{
    @Override
    public String getTitle() {
        return "Uzun Entriler";
    }

    @Override
    public String getTag() {
        return Contract.TAG_SAVE_FOR_LATER;
    }
}
