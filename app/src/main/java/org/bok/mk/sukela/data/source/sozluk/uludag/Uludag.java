package org.bok.mk.sukela.data.source.sozluk.uludag;

import org.bok.mk.sukela.data.source.sozluk.uludag.data.UludagEntry;

import java.io.IOException;
import java.util.List;

public interface Uludag
{
    UludagEntry getEntryByNumber(final int entryNo) throws IOException;

    List<Integer> getHebeIds() throws IOException;

    List<Integer> getDebeIds() throws IOException;

    String createTitleLink(String title);
}
