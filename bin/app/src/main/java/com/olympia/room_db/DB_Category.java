package com.olympia.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Categories")
public class DB_Category {

    @PrimaryKey
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "category_name")
    private String category_name = "";

    @NonNull
    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(@NonNull String category_name) {
        this.category_name = category_name;
    }

    @ColumnInfo(name = "dateAdded")
    private long dateAdded;

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
