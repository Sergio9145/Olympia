package com.olympia.room_db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface KeywordDao {
    @Query("SELECT * FROM keywords")
    List<Keyword> getAll();

    @Query("SELECT * FROM keywords where keyword LIKE  :keyword")
    Keyword findByKeyword(String keyword);

    @Query("SELECT COUNT(*) from keywords")
    int countKeywords();

    @Query("DELETE from keywords")
    int deleteAll();

    @Insert
    void insertAll(Keyword... keywords);

    @Delete
    void delete(Keyword keywords);
}
