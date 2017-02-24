package org.bok.mk.sukela.entry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.bok.mk.sukela.data.DatabaseContract;

/**
 * Created by mk on 13.06.2015.
 */
public class EntryDbHelper extends SQLiteOpenHelper {
    // will be used to tag logs within this class
    private final static String LOG_TAG = EntryDbHelper.class.getSimpleName();

    // Database name
    private final static String DB_NAME = "entry_db";

    // Database version
    private final static int DB_VERSION = 2001;

    public EntryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //
    // Entry table sql schema
    //
    private final static String CREATE_ENTRY_TABLE_SQL = "CREATE TABLE " +
            DatabaseContract.EntryTable.TABLE_NAME + " (" +
            DatabaseContract.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            DatabaseContract.COLUMN_ENTRY_NO + " TEXT, " +
            DatabaseContract.COLUMN_TITLE + " TEXT, " +
            DatabaseContract.COLUMN_TITLE_ID + " TEXT, " +
            DatabaseContract.COLUMN_ENTRY_BODY + " TEXT, " +
            DatabaseContract.COLUMN_USER + " TEXT, " +
            DatabaseContract.COLUMN_DATE_TIME + " TEXT, " +
            DatabaseContract.COLUMN_SOZLUK + " TEXT, " +
            DatabaseContract.COLUMN_TAG + " TEXT" + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_ENTRY_TABLE_SQL);
        Log.i(LOG_TAG, "Creating table with query: " + CREATE_ENTRY_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2001) {
            String backup2000 = "alter table entry rename to temp;";
            String copy2000to2001 = "INSERT INTO entry (entry_no,title,title_id,entry_body,user,date_time,sozluk,tag) SELECT entry_no, title, title_id, entry_body, author, date, sozluk, tag FROM temp where tag ='Sakla';";
            String drop2000 = "drop table temp;";
            String updateSaveTag = "update entry set tag = 'TAG_SAVE_FOR_GOOD' where tag = 'Sakla';";

            db.execSQL(backup2000);
            onCreate(db);
            db.execSQL(copy2000to2001);
            db.execSQL(drop2000);
            db.execSQL(updateSaveTag);

            Log.i("_DB", "Database updated from 2000 to 2001.");
        }
    }

    public Cursor rawQuery(final String sql_query, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql_query, selectionArgs);
    }
}
