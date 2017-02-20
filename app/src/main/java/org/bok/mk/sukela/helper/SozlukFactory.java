package org.bok.mk.sukela.helper;

import android.content.Context;

import org.bok.mk.sukela.source.EksiSozluk;
import org.bok.mk.sukela.source.InciSozluk;
import org.bok.mk.sukela.source.Instela;
import org.bok.mk.sukela.source.Sozluk;
import org.bok.mk.sukela.source.UludagSozluk;

/**
 * Created by mk on 20.12.2016.
 */

public final class SozlukFactory
{
    private SozlukFactory()
    {
    }

    public static Sozluk getSozluk(Meta meta, final Context c)
    {
        switch (meta.getSozluk())
        {
            case EKSI:
                return new EksiSozluk(c, meta);
            case INSTELA:
                return new Instela(c, meta);
            case ULUDAG:
                return new UludagSozluk(c, meta);
            case INCI:
                return new InciSozluk(c, meta);
            default:
                return null;
        }
    }
}
