package org.bok.mk.sukela.data.source.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.bok.mk.sukela.data.model.Entry;
import org.bok.mk.sukela.data.model.EntryList;

import java.util.List;

@Dao
public interface EntryDao
{
    @Query("SELECT * FROM entry2 WHERE tag = :tag order by id")
    List<Entry> getEntries(String tag);

    @Query("DELETE FROM entry2 WHERE tag = :tag")
    int deleteTag(String tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEntries(EntryList entries);

    @Query("SELECT count(*) FROM entry2 where tag = :tag")
    int countEntries(String tag);

    @Query("SELECT DISTINCT tag FROM entry2 where tag LIKE :arsiv")
    List<String> getArsivTags(String arsiv);

    @Query("DELETE FROM entry2 WHERE entryNo = :entryNo and tag = :tag ")
    void deleteEntry(int entryNo, String tag);

    @Query("SELECT * FROM entry2 WHERE title LIKE :query AND tag =:tag  order by id")
    List<Entry> searchTitle(String query, String tag);

    @Query("SELECT * FROM entry2 WHERE body LIKE :query AND tag =:tag  order by id")
    List<Entry> searchEntry(String query, String tag);

    @Query("SELECT * FROM entry2 WHERE user LIKE :query AND tag =:tag order by id")
    List<Entry> searchUser(String query, String tag);
}
