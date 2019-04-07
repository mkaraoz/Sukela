package org.bok.mk.sukela.data.source.sozluk.eksi;

import org.bok.mk.sukela.data.source.sozluk.eksi.data.EksiEntry;

import java.io.IOException;
import java.util.List;

public interface EksiSozluk
{
    /**
     * Numarası verilen entriyi ekşi sözlükten indirir
     *
     * @param entryNo
     * @return
     * @throws IOException
     */
    EksiEntry getEntryByNumber(final int entryNo);

    EksiEntry getBestOfPage(final String title) throws IOException;

    List<Integer> getHebeIds() throws IOException;

    List<Integer> getUserBestOfIds(final String username) throws IOException;

    List<String> getGundemTitles() throws IOException;

    String createTitleLink(String title, int titleId);

    List<EksiEntry> getBestOfYear(int year) throws IOException;

}
