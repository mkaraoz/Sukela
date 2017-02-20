package org.bok.mk.sukela.source;

import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.exception.JerichoFileReadException;

import java.io.File;

public interface SinglePageSource extends Source
{
    EntryList readEntriesFromHtmlFile(File htmlFile) throws JerichoFileReadException;
}
