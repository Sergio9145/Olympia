package com.olympia.room_db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DAO_Matcher {
    @Query("SELECT * FROM Matches")
    List<DB_Matcher> getAll();

    @Query("DELETE from Matches")
    void deleteAll();

    @Insert
    void insertAll(DB_Matcher... matches);
}