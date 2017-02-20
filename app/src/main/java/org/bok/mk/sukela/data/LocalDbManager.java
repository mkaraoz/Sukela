package org.bok.mk.sukela.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.bok.mk.sukela.entry.Entry;
import org.bok.mk.sukela.entry.EntryDbHelper;
import org.bok.mk.sukela.entry.EntryList;
import org.bok.mk.sukela.helper.Contract;
import org.bok.mk.sukela.helper.SozlukEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_DATE_TIME;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_ENTRY_BODY;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_ENTRY_NO;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_SOZLUK;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_TAG;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_TITLE_ID;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_USER;

/**
 * Created by mk on 20.12.2016.
 */

public final class LocalDbManager
{
    private final Context mContext;

    public LocalDbManager(final Context c)
    {
        mContext = c;
    }

    public EntryList getEntriesFromDb(final Uri uri)
    {
        Cursor c = query(uri, DatabaseContract.ENTRY_PROJECTION);
        EntryList list = getEntriesFromCursor(c);
        c.close();
        return list;
    }

    public List<String> getSavedUsers(Uri uri)
    {
        Cursor c = query(uri, new String[]{COLUMN_USER});
        Set<String> users = new HashSet<>();
        if (c.moveToFirst())
        {
            do
            {
                String user = c.getString(c.getColumnIndexOrThrow(DatabaseContract.COLUMN_USER));
                users.add(user);
            } while (c.moveToNext());
        }
        c.close();
        return new ArrayList<>(users);
    }

    public int deleteEntries(final Uri uri)
    {
        return delete(uri);
    }

    public int insertEntriesToDb(List<Entry> entryList)
    {
        ContentResolver resolver = mContext.getContentResolver();
        int numberOfSavedEntries = 0;

        for (Entry e : entryList)
        {
            ContentValues cv = entry2Cv(e);
            Uri contentUri = DatabaseContract.EntryTable.CONTENT_URI;
            Uri uri = resolver.insert(contentUri, cv);
            if (uri != null)
            {
                numberOfSavedEntries++;
            }
        }
        return numberOfSavedEntries;
    }

    public int insertEntriesToDb(EntryList entryList)
    {
        return insertEntriesToDb(entryList.unmodifiableList());
    }

    public int countEntries(final Uri uri)
    {
        String[] projection = new String[]{"count(*) AS count"};
        ContentResolver resolver = mContext.getContentResolver();
        Cursor countCursor = resolver.query(uri, projection, null, null, null);
        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        countCursor.close();
        return count;
    }

    private static EntryList getEntriesFromCursor(final Cursor c)
    {
        EntryList list = new EntryList();

        if (c.moveToFirst())
        {
            do
            {
                int entryNo = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(COLUMN_ENTRY_NO)));
                String title = c.getString(c.getColumnIndexOrThrow(DatabaseContract.COLUMN_TITLE));
                int title_id = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE_ID)));
                String body = c.getString(c.getColumnIndexOrThrow(COLUMN_ENTRY_BODY));
                String user = c.getString(c.getColumnIndexOrThrow(COLUMN_USER));
                String dateTime = c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_TIME));
                String sozluk = c.getString(c.getColumnIndexOrThrow(COLUMN_SOZLUK));
                String tag = c.getString(c.getColumnIndexOrThrow(COLUMN_TAG));

                Entry e = Entry.createEntryWithTag(tag);
                e.setEntryNo(entryNo);
                e.setTitle(title);
                e.setTitleID(title_id);
                e.setBody(body);
                e.setUser(user);
                e.setDateTime(dateTime);
                e.setSozluk(SozlukEnum.getSozlukEnum(sozluk));

                list.add(e);

            } while (c.moveToNext());
        }

        return list;
    }

    private ContentValues entry2Cv(final Entry e)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.COLUMN_USER, e.getUser());
        cv.put(DatabaseContract.COLUMN_DATE_TIME, e.getDateTime());
        cv.put(DatabaseContract.COLUMN_ENTRY_BODY, e.getBody());
        cv.put(DatabaseContract.COLUMN_ENTRY_NO, e.getEntryNo());
        cv.put(DatabaseContract.COLUMN_SOZLUK, e.getSozluk().getName());
        cv.put(DatabaseContract.COLUMN_TITLE, e.getTitle());
        cv.put(DatabaseContract.COLUMN_TAG, e.getTag());
        cv.put(DatabaseContract.COLUMN_TITLE_ID, e.getTitleID());

        return cv;
    }

    private Cursor query(Uri uri, String[] projection)
    {
        return query(uri, projection, null, null, null);
    }

    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        ContentResolver resolver = mContext.getContentResolver();
        return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    private int delete(Uri uri)
    {
        ContentResolver resolver = mContext.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    public Cursor runRawQuery(String query, String[] selectionArgs)
    {
        EntryDbHelper helper = new EntryDbHelper(mContext);
        return helper.rawQuery(query, selectionArgs);
    }

    public void deleteAllUserEntries()
    {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, Contract.TAG_DELETE_ALL_SAVED);
        resolver.delete(uri, null, null);
    }

    public EntryList searchSavedEntries(String query, String searchConfig)
    {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(DatabaseContract.EntryTable.CONTENT_URI, Contract.TAG_SEARCH);
        uri = Uri.withAppendedPath(uri, query);
        uri = Uri.withAppendedPath(uri, searchConfig);
        Cursor c = resolver.query(uri, null, null, null, null);
        return getEntriesFromCursor(c);
    }
}
