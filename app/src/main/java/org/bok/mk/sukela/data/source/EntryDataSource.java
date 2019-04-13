package org.bok.mk.sukela.data.source;

import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;

public interface EntryDataSource
{
    enum Provider
    {
        LOCAL,
        REMOTE
    }

    interface LoadEntryListCallback
    {
        void onEntriesLoaded(EntryList entries);

        void onDataNotAvailable();

        void onProgressUpdate(int progress);

        void onMessageUpdate(String message);

        void onDataLoadStart(Provider provider);

        void onError(String error);

        boolean checkIfOnline();
    }

    int deleteTag(String tag);

    void saveEntries(EntryList entries);

    void deleteEntry(Entry e);

    void getEntries(final String tag, @NonNull LoadEntryListCallback callback);

    void search(String query, boolean title, boolean entry, boolean user, @NonNull LoadEntryListCallback callback);

    void getEntry(final SozlukEnum sozlukEnum, final int entryNo, @NonNull LoadEntryListCallback callback);

    void refreshEntries(final String tag, @NonNull LoadEntryListCallback callback);

    void cancel(final String tag);
}
