package com.olympia;

import java.util.Date;

public class Category {
    private static int last_id = 0;
    public int id;
    public String name;
//    public Date dateAdded;

   public Category() {
        id = last_id++;
        name = "";
//        dateAdded = new Date();
    }
    @Override
    public String toString() {
        return name;
    }
}
