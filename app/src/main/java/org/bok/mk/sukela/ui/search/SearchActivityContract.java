package org.bok.mk.sukela.ui.search;

import org.bok.mk.sukela.data.model.Entry;

interface SearchActivityContract
{
    interface View
    {
        void setAdapter(String[] titles);

        void startEntryScreenActivity(Entry e);
    }

    interface Presenter
    {
        void searchQueryChanged(String text, boolean title, boolean entry, boolean user);

        void uiCreated(String query, boolean title, boolean entry, boolean user);

        void checkChanged(String query, boolean title, boolean entry, boolean user);

        void entryClicked(int position);
    }
}
