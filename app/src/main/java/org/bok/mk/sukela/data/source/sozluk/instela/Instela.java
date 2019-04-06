package org.bok.mk.sukela.data.source.sozluk.instela;

import org.bok.mk.sukela.data.source.sozluk.instela.data.InstelaEntry;

import java.io.IOException;
import java.util.List;

public interface Instela
{
    List<InstelaEntry> getBestOfMonth() throws IOException;

    List<InstelaEntry> getBestOfWeek() throws IOException;

    List<InstelaEntry> getBestOfYesterday() throws IOException;

    List<InstelaEntry> getTop20() throws IOException;

    String createTitleLink(String title, int titleId);

    InstelaEntry getEntryByNumber(int entryNo) throws IOException;
}
