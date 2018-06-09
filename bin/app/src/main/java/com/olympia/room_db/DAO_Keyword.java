package com.olympia.room_db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DAO_Keyword {
    @Query("SELECT * FROM Keywords")
    List<DB_Keyword> getAll();

//    @Query("SELECT * FROM Keywords where keyword LIKE  :keyword")
//    DB_Keyword findByKeyword(String keyword);
//
//    @Query("SELECT COUNT(*) from Keywords")
//    int countKeywords();

    @Query("DELETE from Keywords")
    void deleteAll();

    @Insert
    void insertAll(DB_Keyword... keywords);

//    @Delete
//    void delete(DB_Keyword keywords);
}
