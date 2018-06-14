package com.olympia;

import java.util.Date;

public class Category {
    public static int last_id = -1;
    public int id = -1;
    public String name = "";
    public Date dateAdded;

   public Category() {
        dateAdded = new Date();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Category))return false;
        Category otherCategory = (Category)other;
        return (otherCategory.id == this.id);
    }
}
