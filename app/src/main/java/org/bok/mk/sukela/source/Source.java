package org.bok.mk.sukela.source;

import org.bok.mk.sukela.entry.Entry;

import java.util.List;

/**
 * Created by mk on 10.01.2017.
 */

public interface Source
{
    int saveEntriesToLocalStorage(List<Entry> entries);
}

