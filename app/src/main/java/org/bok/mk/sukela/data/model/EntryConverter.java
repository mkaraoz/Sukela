package org.bok.mk.sukela.data.model;

import org.bok.mk.sukela.data.source.sozluk.eksi.EksiFactory;
import org.bok.mk.sukela.data.source.sozluk.eksi.EksiSozluk;
import org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiEntry;
import org.bok.mk.sukela.data.source.sozluk.instela.Instela;
import org.bok.mk.sukela.data.source.sozluk.instela.InstelaFactory;
import org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaEntry;
import org.bok.mk.sukela.data.source.sozluk.sozlock.data.SozlockEntry;
import org.bok.mk.sukela.data.source.sozluk.uludag.Uludag;
import org.bok.mk.sukela.data.source.sozluk.uludag.UludagFactory;
import org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagEntry;

class EntryConverter
{
    private final static EksiSozluk eksiSozluk = EksiFactory.getInstance();
    private final static Instela instela = InstelaFactory.getInstance();
    private final static Uludag uludag = UludagFactory.getInstance();

    private EntryConverter() {
    }

    static Entry convert(final SozlockEntry se, final String tag) {
        Entry e = new Entry();
        e.setTitle(se.getTitle());
        e.setBody(se.getBody());
        e.setUser(se.getUser());
        e.setDateTime(se.getDate());
        e.setEntryNo(se.getEntryNo());
        e.setTitleUrl(se.getTitleUrl());
        e.setEntryUrl(se.getEntryUrl());
        e.setSozluk(SozlukEnum.EKSI);
        e.setTag(tag);
        return e;
    }

    static Entry convert(final EksiEntry ee, final String tag) {
        Entry e = new Entry();
        e.setTitle(ee.getTitle());
        e.setBody(ee.getBody());
        e.setUser(ee.getUser());
        e.setDateTime(ee.getDateTime());
        e.setEntryNo(ee.getEntryNo());
        e.setTitleUrl(eksiSozluk.createTitleLink(ee.getTitle(), ee.getTitleID()));
        e.setEntryUrl(ee.getEntryUrl());
        e.setSozluk(SozlukEnum.EKSI);
        e.setTag(tag);
        return e;
    }


    static Entry convert(final InstelaEntry ie, String tag) {
        Entry e = new Entry();
        e.setTitle(ie.getTitle());
        e.setBody(ie.getBody());
        e.setUser(ie.getUser());
        e.setDateTime(ie.getDateTime());
        e.setEntryNo(ie.getEntryNo());
        e.setTitleUrl(instela.createTitleLink(ie.getTitle(), ie.getTitleID()));
        e.setEntryUrl(ie.getEntryUrl());
        e.setSozluk(SozlukEnum.INSTELA);
        e.setTag(tag);
        return e;
    }

    static Entry convert(UludagEntry ue, String tag) {
        Entry e = new Entry();
        e.setTitle(ue.getTitle());
        e.setBody(ue.getBody());
        e.setUser(ue.getUser());
        e.setDateTime(ue.getDateTime());
        e.setEntryNo(ue.getEntryNo());
        e.setTitleUrl(uludag.createTitleLink(ue.getTitle()));
        e.setEntryUrl(ue.getEntryUrl());
        e.setSozluk(SozlukEnum.ULUDAG);
        e.setTag(tag);
        return e;
    }
}
