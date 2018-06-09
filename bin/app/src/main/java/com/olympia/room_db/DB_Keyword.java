package com.olympia.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "Keywords")
public class DB_Keyword {

    @PrimaryKey
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "keyword")
    private String keyword = "";

    @NonNull
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(@NonNull String keyword) {
        this.keyword = keyword;
    }

//    @ColumnInfo(name = "dateAdded")
//    private Date dateAdded;
//
//    @NonNull
//    public Date getDateAdded() {
//        return dateAdded;
//    }
//
//    public void setDateAdded(@NonNull Date dateAdded) {
//        this.dateAdded = dateAdded;
//    }
}
