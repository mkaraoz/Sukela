package org.bok.mk.sukela.data.source;

import android.support.annotation.NonNull;

import org.bok.mk.sukela.data.model.Contract;
import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.model.SozlukEnum;

import static org.bok.mk.sukela.util.Preconditions.checkNotNull;

public class EntryRepo implements EntryDataSource
{
    private static EntryRepo INSTANCE = null;

    private final EntryDataSource mEntriesRemoteDataSource;
    private final EntryDataSource mEntriesLocalDataSource;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    private boolean mDbUpdateRequired = false;

    private EntryRepo(@NonNull EntryDataSource tasksRemoteDataSource, @NonNull EntryDataSource entryLocalDataSource) {
        mEntriesRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mEntriesLocalDataSource = checkNotNull(entryLocalDataSource);
    }

    public static EntryRepo getInstance(EntryDataSource tasksRemoteDataSource, EntryDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new EntryRepo(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public int deleteTag(String tag) {
        mEntriesRemoteDataSource.deleteTag(tag);
        return mEntriesLocalDataSource.deleteTag(tag);
    }

    @Override
    public void saveEntries(EntryList entries) {
        mEntriesLocalDataSource.saveEntries(entries);
        mEntriesRemoteDataSource.saveEntries(entries);
    }

    @Override
    public void getEntries(final String tag, @NonNull final LoadEntryListCallback callback) {

        if (mDbUpdateRequired) {
            getEntriesFromRemoteDataSource(tag, callback);
            return;
        }

        // Query the local storage if available. If not, query the network.
        mEntriesLocalDataSource.getEntries(tag, new LoadEntryListCallback()
        {
            @Override
            public void onEntriesLoaded(EntryList entries) {
                callback.onEntriesLoaded(entries);
            }

            @Override
            public void onDataNotAvailable() {
                if (tag.equals(Contract.TAG_SAVE_FOR_GOOD) || tag.equals(
                        Contract.TAG_SAVE_FOR_LATER)) {
                    callback.onDataNotAvailable();
                }
                else {
                    getEntriesFromRemoteDataSource(tag, callback);
                }
            }

            @Override
            public void onProgressUpdate(int progress) {
                callback.onProgressUpdate(progress);
            }

            @Override
            public void onMessageUpdate(String message) {
                callback.onMessageUpdate(message);
            }

            @Override
            public void onDataLoadStart(boolean fromLocal) {
                callback.onDataLoadStart(fromLocal);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }

            @Override
            public boolean checkIfOnline() {
                return callback.checkIfOnline();
            }

        });
    }

    @Override
    public void getEntry(SozlukEnum sozlukEnum, int entryNo, @NonNull LoadEntryListCallback cb) {
        mEntriesRemoteDataSource.getEntry(sozlukEnum, entryNo, new LoadEntryListCallback()
        {
            @Override
            public void onEntriesLoaded(EntryList entries) {
                // save entry
                mEntriesLocalDataSource.saveEntries(entries);
                cb.onEntriesLoaded(entries);
            }

            @Override
            public void onDataNotAvailable() {
                cb.onDataNotAvailable();
            }

            @Override
            public void onProgressUpdate(int progress) {
                cb.onProgressUpdate(progress);
            }

            @Override
            public void onMessageUpdate(String message) {
                cb.onMessageUpdate(message);
            }

            @Override
            public void onDataLoadStart(boolean fromLocal) {
                cb.onDataLoadStart(fromLocal);
            }

            @Override
            public void onError(String error) {
                cb.onError(error);
            }

            @Override
            public boolean checkIfOnline() {
                return cb.checkIfOnline();
            }
        });
    }

    private void getEntriesFromRemoteDataSource(final String tag, @NonNull final LoadEntryListCallback cb) {
        mEntriesRemoteDataSource.getEntries(tag, new LoadEntryListCallback()
        {
            @Override
            public void onEntriesLoaded(EntryList entries) {
                mDbUpdateRequired = false;
                refreshLocalDataSource(entries);
                cb.onEntriesLoaded(entries);
            }

            @Override
            public void onDataNotAvailable() {
                cb.onDataNotAvailable();
            }

            @Override
            public void onProgressUpdate(int progress) {
                cb.onProgressUpdate(progress);
            }

            @Override
            public void onMessageUpdate(String message) {
                cb.onMessageUpdate(message);
            }

            @Override
            public void onDataLoadStart(boolean fromLocal) {
                cb.onDataLoadStart(fromLocal);
            }

            @Override
            public void onError(String error) {
                cb.onError(error);
            }

            @Override
            public boolean checkIfOnline() {
                return cb.checkIfOnline();
            }
        });
    }

    private void refreshLocalDataSource(EntryList entries) {
        if (entries.size() > 0) {
            mEntriesLocalDataSource.deleteTag(entries.get(0).getTag());
            mEntriesLocalDataSource.saveEntries(entries);
        }
    }

    @Override
    public void refreshEntries(final String tag, @NonNull final LoadEntryListCallback callback) {
        mDbUpdateRequired = true; // will force remote repo
        getEntries(tag, callback);
    }

    @Override
    public void cancel(String tag) {
        mDbUpdateRequired = false; // update cancelled, must use db
        mEntriesRemoteDataSource.cancel(tag);
        mEntriesLocalDataSource.cancel(tag);
    }

    @Override
    public void deleteEntry(Entry e) {
        mEntriesLocalDataSource.deleteEntry(e);
    }

    @Override
    public void search(String query, boolean title, boolean entry, boolean user, @NonNull LoadEntryListCallback callback) {
        mEntriesLocalDataSource.search(query, title, entry, user, new LoadEntryListCallback()
        {
            @Override
            public void onEntriesLoaded(EntryList entries) {
                callback.onEntriesLoaded(entries);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onProgressUpdate(int progress) {
                callback.onProgressUpdate(progress);
            }

            @Override
            public void onMessageUpdate(String message) {
                callback.onMessageUpdate(message);
            }

            @Override
            public void onDataLoadStart(boolean fromLocal) {
                callback.onDataLoadStart(fromLocal);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }

            @Override
            public boolean checkIfOnline() {
                return true;
            }
        });
    }
}
