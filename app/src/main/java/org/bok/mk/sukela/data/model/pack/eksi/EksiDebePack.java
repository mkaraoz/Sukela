package org.bok.mk.sukela.data.model.pack.eksi;

import static org.bok.mk.sukela.data.model.Contract.TAG_EKSI_DEBE;

public class EksiDebePack extends EksiPack
{
    @Override
    public String getTitle() {
        return "Dünün en beğenilenleri";
    }

    @Override
    public String getTag() {
        return TAG_EKSI_DEBE;
    }
}
