package org.bok.mk.sukela.data.model.pack.uludag;

import org.bok.mk.sukela.data.model.Contract;

public class UludagDebePack extends UludagPack
{
    @Override
    public String getTitle() {
        return "Dünün en beğenilenleri";
    }

    @Override
    public String getTag() {
        return Contract.TAG_ULUDAG_YESTERDAY;
    }
}
