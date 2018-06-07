package com.olympia;

import java.util.ArrayList;
import java.util.HashMap;

public class Vocabulary {
    public static HashMap<String, Node> nodes = new HashMap<>();
    public static ArrayList<String> keywords = new ArrayList<>();
    public static ArrayList<Category> categories = new ArrayList<>();
    public static HashMap<String, ArrayList<Category>> map = new HashMap<>();

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
    private Vocabulary() {}
}
