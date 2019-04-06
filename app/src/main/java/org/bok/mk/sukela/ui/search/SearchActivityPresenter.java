package org.bok.mk.sukela.ui.search;

import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;
import org.bok.mk.sukela.data.source.EntryDataSource;
import org.bok.mk.sukela.data.source.EntryRepo;
import org.bok.mk.sukela.ui.base.BasePresenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivityPresenter extends BasePresenter<SearchActivityContract.View> implements SearchActivityContract.Presenter
{
    private final EntryRepo mEntryRepo;
    private EntryList mEntries = new EntryList();

    SearchActivityPresenter(EntryRepo repo) {
        mEntryRepo = repo;
    }

    @Override
    public void searchQueryChanged(String query, boolean title, boolean entry, boolean user) {
        if (query.length() >= 3) {
            ExecutorService e = Executors.newSingleThreadExecutor();
            e.execute(() -> search(query, title, entry, user));
            e.shutdown();
        }
        else {
            mView.setAdapter(new String[]{});
        }
    }

    private void search(String query, boolean title, boolean entry, boolean user) {
        mEntryRepo.search(query, title, entry, user, new EntryDataSource.LoadEntryListCallback()
        {
            @Override
            public void onEntriesLoaded(EntryList entries) {
                entries.sort();
                mEntries = entries;
                String[] titles = new String[entries.size()];
                for (int i = 0; i < titles.length; i++)
                    titles[i] = entries.get(i).getTitle();
                mView.setAdapter(titles);
            }

            @Override
            public void onDataNotAvailable() {
                mView.setAdapter(new String[]{});
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onMessageUpdate(String message) {

            }

            @Override
            public void onDataLoadStart(boolean fromLocal) {

            }

            @Override
            public void onError(String error) {

            }

            @Override
            public boolean checkIfOnline() {
                return false;
            }
        });
    }

    @Override
    public void uiCreated(String query, boolean title, boolean entry, boolean user) {
        //        mView.checkTitle(true);
        //        mView.checkEntry(true);
        //        mView.checkUser(true);
        //
        //        mView.setAdapter(new String[]{});
        searchQueryChanged(query, title, entry, user);
    }

    @Override
    public void checkChanged(String query, boolean title, boolean entry, boolean user) {
        this.searchQueryChanged(query, title, entry, user);
    }

    @Override
    public void entryClicked(int position) {
        Entry e = mEntries.get(position);
        mView.startEntryScreenActivity(e);
    }

}
