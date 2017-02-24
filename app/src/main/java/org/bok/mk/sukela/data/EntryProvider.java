package org.bok.mk.sukela.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.bok.mk.sukela.entry.EntryDbHelper;
import org.bok.mk.sukela.helper.Contract;

import java.util.List;

import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_ENTRY_BODY;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_ENTRY_NO;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_TAG;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_TITLE;
import static org.bok.mk.sukela.data.DatabaseContract.COLUMN_USER;
import static org.bok.mk.sukela.data.DatabaseContract.CONTENT_AUTHORITY;

/**
 * Created by mk on 24.12.2016.
 */

public class EntryProvider extends ContentProvider {
    private EntryDbHelper mDbHelper;
    private static final UriMatcher uriMatcher;

    private static final int URI_ENTRY_TAG = 0;
    private static final int URI_USER_LIST_TAG = 1;
    private static final int URI_USER_ENTRY_TAG = 2;
    private static final int URI_ALL_SAVED_ENTRIES = 3;
    private static final int URI_SINGLE_SAVED_LONG = 4;
    private static final int URI_SINGLE_SAVED_GOOD = 5;
    private static final int URI_SEARCH = 6;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_SEARCH + "/*/*", URI_SEARCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_DELETE_SINGLE_LONG + "/#", URI_SINGLE_SAVED_LONG);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_DELETE_SINGLE_GOOD + "/#", URI_SINGLE_SAVED_GOOD);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_DELETE_ALL_SAVED, URI_ALL_SAVED_ENTRIES);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_USER + "/*", URI_USER_ENTRY_TAG);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_USER, URI_USER_LIST_TAG);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/*", URI_ENTRY_TAG);


        //        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_SAKLA_CHECK + "/#/*", URI_SAKLA_CHECK);
        //        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_SEARCH + "/*", URI_SEARCH);
        //        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_SAKLA, URI_SAKLA_ALL);
        //        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_USER_NAME, URI_USER_NAME);
        //        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_USER_NAME + "/*", URI_DELETE_USER);
        //        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.EntryTable.TABLE_NAME + "/" + Contract.TAG_SAKLA + "/#/*", URI_DELETE_SINGLE_ENTRY_FROM_SAKLA); // org.bok.mk.sukela.provider/entry/sakla/position
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new EntryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int uri_code = uriMatcher.match(uri);
        switch (uri_code) {
            case 0:
                return DatabaseContract.EntryTable.ITEM_SINGLE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int uriCode = uriMatcher.match(uri);
        switch (uriCode) {
            case URI_ENTRY_TAG: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                String tag = uri.getLastPathSegment();
                selection = COLUMN_TAG + " = ?";
                selectionArgs = new String[]{tag};
                sortOrder = DatabaseContract.ID;
                queryBuilder.setTables(DatabaseContract.EntryTable.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case URI_USER_LIST_TAG: {
                boolean distinct = true;
                projection = new String[]{COLUMN_USER};
                selection = COLUMN_TAG + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                String groupBy = COLUMN_USER;
                String having = null;
                sortOrder = null;
                String limit = null;
                cursor = db.query(distinct, DatabaseContract.EntryTable.TABLE_NAME, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
                break;
            }
            case URI_USER_ENTRY_TAG: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                String userName = uri.getLastPathSegment();
                selection = COLUMN_TAG + " = ? and " + COLUMN_USER + " = ?";
                selectionArgs = new String[]{Contract.TAG_USER, userName};
                sortOrder = DatabaseContract.ID;
                queryBuilder.setTables(DatabaseContract.EntryTable.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case URI_SEARCH: {
                List<String> paths = uri.getPathSegments();
                int size = paths.size();
                String query = paths.get(size - 2);
                String scope = paths.get(size - 1);

                boolean distinct = true;
                projection = DatabaseContract.ENTRY_PROJECTION;
                StringBuilder sb = new StringBuilder();
                sb.append(COLUMN_TAG + " = ? and ( 0 ");
                char[] c = scope.toCharArray();
                char title = c[0];
                char body = c[1];
                char user = c[2];
                int count = 0;
                if (title == '1') {
                    sb.append("OR " + COLUMN_TITLE + " LIKE ? ");
                    count++;
                }
                if (body == '1') {
                    sb.append("OR " + COLUMN_ENTRY_BODY + " LIKE ? ");
                    count++;
                }
                if (user == '1') {
                    sb.append("OR " + COLUMN_USER + " LIKE ? ");
                    count++;
                }
                sb.append(")");

                selection = sb.toString();
                //selection = COLUMN_TAG + " = ? and ( 0 OR " + COLUMN_TITLE + " LIKE ? OR " + COLUMN_ENTRY_BODY + " LIKE ? OR " + COLUMN_USER + " LIKE ?)";
                selectionArgs = new String[1 + count];
                selectionArgs[0] = Contract.TAG_SAVE_FOR_GOOD;
                for (int i = 1; i < selectionArgs.length; i++)
                    selectionArgs[i] = "%" + query + "%";

                //selectionArgs = new String[]{Contract.TAG_SAVE_FOR_GOOD, "%" + query + "%", "%" + query + "%", "%" + query + "%"};

                String groupBy = null;
                String having = null;
                sortOrder = null;
                String limit = null;
                cursor = db.query(distinct, DatabaseContract.EntryTable.TABLE_NAME, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Tell the cursor what uri to watch, so it knows when its source data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String tableName = uri.getLastPathSegment();
        long rowId = db.insertOrThrow(tableName, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(DatabaseContract.BASE_CONTENT_URI.buildUpon().appendPath(tableName).build(), rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int uriCode = uriMatcher.match(uri);
        int deletionCount = 0;

        switch (uriCode) {
            case URI_ENTRY_TAG: // bu tagle ilişkili ne varsa sil
            {
                String tag = uri.getLastPathSegment();
                selection = COLUMN_TAG + " = ?";
                selectionArgs = new String[]{tag};
                deletionCount = db.delete(DatabaseContract.EntryTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case URI_USER_ENTRY_TAG: {
                String userName = uri.getLastPathSegment();
                selection = COLUMN_TAG + " = ? and " + COLUMN_USER + " = ?";
                selectionArgs = new String[]{Contract.TAG_USER, userName};
                deletionCount = db.delete(DatabaseContract.EntryTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case URI_ALL_SAVED_ENTRIES: {
                selection = COLUMN_TAG + " = ? or " + COLUMN_TAG + " = ?";
                selectionArgs = new String[]{Contract.TAG_SAVE_FOR_GOOD, Contract.TAG_SAVE_FOR_LATER};
                deletionCount = db.delete(DatabaseContract.EntryTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case URI_SINGLE_SAVED_LONG: {
                selection = COLUMN_TAG + " = ? and " + COLUMN_ENTRY_NO + " = ?";
                String entryNo = uri.getLastPathSegment();
                String[] args = new String[]{Contract.TAG_SAVE_FOR_LATER, entryNo};
                deletionCount = db.delete(DatabaseContract.EntryTable.TABLE_NAME, selection, args);
                break;
            }
            case URI_SINGLE_SAVED_GOOD: {
                selection = COLUMN_TAG + " = ? and " + COLUMN_ENTRY_NO + " = ?";
                String entryNo = uri.getLastPathSegment();
                String[] args = new String[]{Contract.TAG_SAVE_FOR_GOOD, entryNo};
                deletionCount = db.delete(DatabaseContract.EntryTable.TABLE_NAME, selection, args);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return deletionCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
