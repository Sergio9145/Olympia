package com.olympia.room_db;

import com.olympia.Category;
import com.olympia.Node;
import com.olympia.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomDbFacade {
    private static final Object mutex = new Object();
    public static void saveAllKeywords(final AppDatabase db) {
        synchronized (mutex) {
            db.dao_keyword().deleteAll();
            for (String s : Vocabulary.keywords) {
                DB_Keyword k = new DB_Keyword();
                k.setKeyword(s);
                db.dao_keyword().insertAll(k);
            }
            db.dao_category().deleteAll();
            for (Category s : Vocabulary.categories) {
                DB_Category k = new DB_Category();
                k.setId(s.id);
                k.setCategory_name(s.name);
//                k.setDateAdded(s.dateAdded);
                db.dao_category().insertAll(k);
            }
        }
    }

    public static void getAllKeywords(final AppDatabase db) {
        synchronized (mutex) {
            Vocabulary.nodes = new HashMap<>();

            List<DB_Keyword> db_keywords = db.dao_keyword().getAll();
            ArrayList<String> app_keywords = new ArrayList<>();
            for (DB_Keyword k : db_keywords) {
                String s = k.getKeyword();
                app_keywords.add(s);
                Vocabulary.nodes.put(s, new Node());
            }
            Vocabulary.keywords = app_keywords;

            List<DB_Category> db_categories = db.dao_category().getAll();
            ArrayList<Category> app_categories = new ArrayList<>();
            for (DB_Category k : db_categories) {
                Category c = new Category();
                c.id = k.getId();
                c.name = k.getCategory_name();
//                c.dateAdded = k.getDateAdded();
                app_categories.add(c);
            }

//            for (Category c : app_categories) {
//                if (Category.last_id <= c.id) {
//                    Category.last_id = c.id + 1;
//                }
//            }
            Vocabulary.categories = app_categories;
        }
    }

    public static void deleteAll(final AppDatabase db) {
        synchronized (mutex) {
            db.dao_keyword().deleteAll();
        }
    }
}
