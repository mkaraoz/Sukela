package org.bok.mk.sukela.data.source.repo;

import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;
import org.bok.mk.sukela.data.source.EntryDataSource;
import org.bok.mk.sukela.data.source.db.EntryDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalEntryRepository implements EntryDataSource
{
    private static volatile LocalEntryRepository INSTANCE;

    private final EntryDao mEntryDao;

    // Prevent direct instantiation.
    private LocalEntryRepository(@NonNull EntryDao entryDao) {
        mEntryDao = entryDao;
    }

    public static LocalEntryRepository getInstance(@NonNull EntryDao entryDao) {
        if (INSTANCE == null) {
            synchronized (LocalEntryRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalEntryRepository(entryDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public int deleteTag(String tag) {
        return mEntryDao.deleteTag(tag);
    }

    @Override
    public void saveEntries(EntryList entries) {
        mEntryDao.saveEntries(entries);
    }

    @Override
    public void getEntries(final String tag, @NonNull LoadEntryListCallback callback) {
        int count = mEntryDao.countEntries(tag);
        if (count <= 0) {
            callback.onDataNotAvailable();
            return;
        }
        else { callback.onDataLoadStart(true); }
        List<Entry> entries = mEntryDao.getEntries(tag);
        callback.onEntriesLoaded(EntryList.fromList(entries));
    }

    @Override
    public void getEntry(SozlukEnum sozlukEnum, int entryNo, @NonNull LoadEntryListCallback callback) {
        // Not needed here, required for remote
    }

    @Override
    public void refreshEntries(String tag, @NonNull LoadEntryListCallback callback) {
        // Not needed, logic implemented in EntryRepo
    }

    @Override
    public void cancel(String tag) {
        // Not needed here, required for remote
    }

    @Override
    public void deleteEntry(Entry e) {
        mEntryDao.deleteEntry(e.getEntryNo(), e.getTag());
    }

    @Override
    public void search(String query, boolean title, boolean entry, boolean user, @NonNull LoadEntryListCallback callback) {
        Set<Entry> entries = new HashSet<>();
        query = "%" + query + "%";
        if (title) {
            entries.addAll(mEntryDao.searchTitle(query, Contract.TAG_SAVE_FOR_GOOD));
        }
        if (entry) { entries.addAll(mEntryDao.searchEntry(query, Contract.TAG_SAVE_FOR_GOOD)); }
        if (user) { entries.addAll(mEntryDao.searchUser(query, Contract.TAG_SAVE_FOR_GOOD)); }

        if (entries.size() <= 0) {
            callback.onDataNotAvailable();
            return;
        }
        else { callback.onDataLoadStart(true); }
        callback.onEntriesLoaded(EntryList.fromList(new ArrayList<>(entries)));
    }
}
