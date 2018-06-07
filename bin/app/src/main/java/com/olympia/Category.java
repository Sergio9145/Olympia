package com.olympia;

public class Category {
    private static int last_id = 0;
    public int id;
    public String name;

    Category() {
        id = last_id++;
        name = "";
    }
    @Override
    public String toString() {
        return name;
    }
}
