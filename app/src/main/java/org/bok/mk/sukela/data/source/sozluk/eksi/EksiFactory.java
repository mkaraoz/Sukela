package org.bok.mk.sukela.data.source.sozluk.eksi;

public class EksiFactory
{
    public static EksiSozluk getInstance() {
        return new EksiRunner();
    }
}
