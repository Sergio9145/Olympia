package com.olympia.room_db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DAO_Category {
    @Query("SELECT * FROM Categories")
    List<DB_Category> getAll();

//    @Query("SELECT * FROM Categories where category LIKE :category")
//    DB_Category findByCategory(String category);
//
//    @Query("SELECT COUNT(*) from Categories")
//    int countCategories();

    @Query("DELETE from Categories")
    void deleteAll();

    @Insert
    void insertAll(DB_Category... categories);

//    @Delete
//    void delete(DB_Category categories);
}

