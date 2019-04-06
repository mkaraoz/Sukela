package org.bok.mk.sukela.data.model.pack.eksi;

import org.bok.mk.sukela.data.model.Contract;

public class EksiGundemPack extends EksiPack
{
    @Override
    public String getTitle() {
        return "Gündem";
    }

    @Override
    public String getTag() {
        return Contract.TAG_EKSI_GUNDEM;
    }
}
