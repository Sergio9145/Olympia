package com.olympia.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Keywords")
public class Keyword {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "keyword")
    private String keyword = "";

    @NonNull
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(@NonNull String keyword) {
        this.keyword = keyword;
    }
}
