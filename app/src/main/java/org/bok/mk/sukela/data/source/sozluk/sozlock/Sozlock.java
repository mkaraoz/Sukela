package org.bok.mk.sukela.data.source.sozluk.sozlock;

import org.bok.mk.sukela.data.source.sozluk.sozlock.data.SozlockEntry;

import java.io.IOException;
import java.util.List;

public interface Sozlock
{
    List<SozlockEntry> getDebe() throws IOException;

    List<SozlockEntry> getDebeByDate(final String date) throws IOException;
}
