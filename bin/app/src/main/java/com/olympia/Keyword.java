package com.olympia;

import java.util.Date;

public class Keyword {
    public static int last_id = -1;
    public int id = -1;
    public String name = "";
    public Date dateAdded;

    public Keyword() {
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
        if (!(other instanceof Keyword))return false;
        Keyword otherKeyword = (Keyword)other;
        return (otherKeyword.id == this.id);
    }
}
