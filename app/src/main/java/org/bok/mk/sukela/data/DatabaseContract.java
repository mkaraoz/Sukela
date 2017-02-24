package org.bok.mk.sukela.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mk on 13.06.2015.
 */
public class DatabaseContract {
    // Android-internal name of the Content Provider
    static final String CONTENT_AUTHORITY = "org.bok.mk.sukela.provider";

    // Base Content Uri
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Define table columns
    public static final String ID = BaseColumns._ID;
    public static final String COLUMN_ENTRY_NO = "entry_no";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TITLE_ID = "title_id";
    public static final String COLUMN_ENTRY_BODY = "entry_body";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_DATE_TIME = "date_time";
    public static final String COLUMN_SOZLUK = "sozluk";
    public static final String COLUMN_TAG = "tag";


    // projection that defines the columns that will be returned for each row
    static final String[] ENTRY_PROJECTION = new String[]{
                /*0*/ ID,
                /*1*/ COLUMN_ENTRY_NO,
                /*2*/ COLUMN_TITLE,
                /*3*/ COLUMN_TITLE_ID,
                /*4*/ COLUMN_ENTRY_BODY,
                /*5*/ COLUMN_USER,
                /*6*/ COLUMN_DATE_TIME,
                /*7*/ COLUMN_SOZLUK,
                /*8*/ COLUMN_TAG};

    //
    // Entry Table
    //
    public static class EntryTable implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "entry";

        // Content Uri - points to entry all table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // Use MIME type
        public static final String ITEM_LIST = "vnd.android.cursor.dir/vnd.org.bok.mk.sukela.data.provider.entries";
        public static final String ITEM_SINGLE = "vnd.android.cursor.item/vnd.org.bok.mk.sukela.data.provider.entry";

    }
}
