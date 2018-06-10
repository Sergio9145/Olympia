package com.olympia.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Matches")
public class DB_Matcher {

    @PrimaryKey
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "word_id")
    private int word_id = 0;

    public int getWord_id() {
        return word_id;
    }

    public void setWord_id(int wid) {
        this.word_id = wid;
    }

    @ColumnInfo(name = "category_id")
    private int category_id = 0;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int cid) {
        this.category_id = cid;
    }

//    @ColumnInfo(category_name = "dateCreated")
//    private Date dateCreated;
//
//    @NonNull
//    public Date getDateAdded() {
//        return dateCreated;
//    }
//
//    public void setDateAdded(@NonNull Date dateCreated) {
//        this.dateCreated = dateCreated;
//    }
}
