package org.bok.mk.sukela.source;

import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.callbacks.MultiFileDownloadCallback;

import java.io.IOException;
import java.util.List;

public interface MultiPageSource extends Source {
    List<String> getEntryNumbersFromUrl(String url) throws IOException;

    EntryList downloadEntries(List<String> entryIds, MultiFileDownloadCallback feedback) throws IOException;
}
