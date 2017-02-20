package org.bok.mk.sukela.entry;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.bok.mk.sukela.data.LocalDbManager;
import org.bok.mk.sukela.helper.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mk on 18.12.2016.
 */

public final class EntryManager
{
    private final LocalDbManager dbManager;

    private EntryManager(final Context c)
    {
        this.dbManager = new LocalDbManager(c);
    }

    public static EntryManager getManager(final Context c)
    {
        return new EntryManager(c);
    }

    public int countEntries(final Uri uri)
    {
        int entryCount = dbManager.countEntries(uri);
        return entryCount;
    }

    public int countUserEntries(final String userName)
    {
        String query = "select count(*) AS count from entry where tag = ? and user = ?;";
        String selectionArgs[] = new String[]{Contract.TAG_USER, userName};
        return countEntries(query, selectionArgs);
    }

    public EntryList getStoredEntries(final Uri uri)
    {
        return dbManager.getEntriesFromDb(uri);
    }

    public int insertEntriesToDb(List<Entry> entries)
    {
        int savedEntryCount = dbManager.insertEntriesToDb(entries);
        return savedEntryCount;
    }

    public boolean saveEntry(final Entry e)
    {
        boolean isAlreadySaved = checkIfAlreadySaved(e);
        if (isAlreadySaved)
        {
            return true;
        }

        return insertEntriesToDb(new ArrayList<>(Collections.singletonList(e))) == 1;
    }

    private boolean checkIfAlreadySaved(final Entry e)
    {
        String query = "select count(*) AS count from entry where tag = ? and sozluk = ? and entry_no = ?;";
        String selectionArgs[] = new String[]{e.getTag(), e.getSozluk().getName(), String.valueOf(e.getEntryNo())};
        return countEntries(query, selectionArgs) > 0;
    }

    private int countEntries(String query, String[] selectionArgs)
    {
        Cursor countCursor = dbManager.runRawQuery(query, selectionArgs);
        countCursor.moveToFirst();
        int entryCount = countCursor.getInt(0);
        countCursor.close();
        return entryCount;
    }

    public EntryList searchSavedEntries(final String query, final String searchConfig)
    {
        return dbManager.searchSavedEntries(query, searchConfig);
    }
}
