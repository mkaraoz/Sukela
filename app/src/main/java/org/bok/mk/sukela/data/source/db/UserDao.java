package org.bok.mk.sukela.data.source.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.bok.mk.sukela.data.model.User;

import java.util.List;

@Dao
public interface UserDao
{
    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveUser(User user);

    @Query("Delete from users where userName = :username")
    void deleteUser(String username);
}
