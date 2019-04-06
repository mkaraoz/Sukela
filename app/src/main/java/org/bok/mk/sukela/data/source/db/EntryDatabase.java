package org.bok.mk.sukela.data.source.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.util.SozlukEnumConverter;

@Database(entities = {Entry.class}, version = 2002, exportSchema = false)
@TypeConverters({SozlukEnumConverter.class})
public abstract class EntryDatabase extends RoomDatabase
{
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "entry_db";
    private static EntryDatabase INSTANCE;

    public static EntryDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        EntryDatabase.class, EntryDatabase.DATABASE_NAME).addMigrations(
                        MIGRATION_2001_2002).build();
            }
            return INSTANCE;
        }
    }

    private static final Migration MIGRATION_2001_2002 = new Migration(2001, 2002)
    {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("delete from entry where tag not in ('TAG_SAVE_FOR_GOOD');");
            db.execSQL("delete from entry where sozluk = 'Inci Sözlük';"); // inciyi kaldırdım
            db.execSQL(
                    "CREATE TABLE `entry2` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `body` TEXT, `user` TEXT, `dateTime` TEXT, `entryNo` INTEGER NOT NULL, `titleUrl` TEXT, `sozluk` TEXT, `tag` TEXT, `entryUrl` TEXT);");
            db.execSQL(
                    "INSERT INTO entry2 (title,body,user,dateTime,entryNo,titleUrl, entryUrl, sozluk,tag) SELECT title, entry_body, user, date_time, entry_no, title_id, entry_no, sozluk, tag FROM entry;");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = 'https://eksisozluk.com/' || (select entry2.title WHERE sozluk = 'Ekşi Sözlük') where sozluk = 'Ekşi Sözlük';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = 'https://tr.instela.com/' || (select entry2.title WHERE sozluk = 'Instela') || '--' || titleUrl WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, 'ı', 'i')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, 'ü', 'i')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, 'ğ', 'g')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, 'ş', 's')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, 'ö', 'o')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, 'ç', 'c')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, ' ', '-')  WHERE sozluk = 'Instela';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = 'https://m.uludagsozluk.com/k/' || (select entry2.title WHERE sozluk = 'Uludağ Sözlük')  WHERE sozluk = 'Uludağ Sözlük';");
            db.execSQL(
                    "UPDATE entry2 SET titleUrl = REPLACE(titleUrl, ' ', '-')  WHERE sozluk = 'Uludağ Sözlük';");
            db.execSQL(
                    "UPDATE entry2 SET entryUrl = 'https://eksisozluk.com/entry/' || entryUrl where sozluk = 'Ekşi Sözlük';");
            db.execSQL(
                    "UPDATE entry2 SET entryUrl = 'https://m.uludagsozluk.com/e/' || entryUrl where sozluk = 'Uludağ Sözlük';");
            db.execSQL(
                    "UPDATE entry2 SET entryUrl = 'https://www.itusozluk.com/goster.php/%40' || entryUrl where sozluk = 'Instela';");
            db.execSQL("drop table entry;");
        }
    };

    public abstract EntryDao entryDao();
}
