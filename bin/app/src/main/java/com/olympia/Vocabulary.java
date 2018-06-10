package com.olympia;

import java.util.ArrayList;
import java.util.HashMap;

public class Vocabulary {
    private Vocabulary() {} //* To prevent from instantiating

    public static HashMap<String, Node> nodes = new HashMap<>();
    public static ArrayList<Keyword> keywords = new ArrayList<>();
    public static ArrayList<Category> categories = new ArrayList<>();
    public static HashMap<Keyword, ArrayList<Category>> map = new HashMap<>();

    public static boolean containsWord(String w) {
        boolean result = false;
        for (Keyword k : keywords) {
            if (k.name.equalsIgnoreCase(w)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean containsCategory(String cat) {
        boolean result = false;
        for (Category c : categories) {
            if (c.name.equalsIgnoreCase(cat)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static Keyword getWordById(int id) {
        Keyword result = null;
        for (Keyword k : keywords) {
            if (k.id == id) {
                result = k;
                break;
            }
        }
        return result;
    }

    public static Category getCategoryById(int id) {
        Category result = null;
        for (Category c : categories) {
            if (c.id == id) {
                result = c;
                break;
            }
        }
        return result;
    }
}
