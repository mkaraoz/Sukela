package org.bok.mk.sukela.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users")
public class User implements Serializable
{
    @PrimaryKey(autoGenerate = true) private int id;
    private final String userName;

    @Ignore
    public User(String name) {
        this.userName = name;
    }

    // for room db
    public User(int id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}
