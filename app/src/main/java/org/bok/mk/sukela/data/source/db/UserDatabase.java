package org.bok.mk.sukela.data.source.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import org.bok.mk.sukela.data.model.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase
{
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "user.db";
    private static UserDatabase INSTANCE;

    public static UserDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        UserDatabase.class, UserDatabase.DATABASE_NAME).build();
            }
            return INSTANCE;
        }
    }

    public abstract UserDao userDao();
}
