package com.olympia.room_db;

import com.olympia.Category;
import com.olympia.Keyword;
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
            for (Keyword s : Vocabulary.keywords) {
                DB_Keyword k = new DB_Keyword();
                k.setId(s.id);
                k.setKeyword(s.name);
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
            int match_id = 0;
            db.dao_matcher().deleteAll();
            for (HashMap.Entry<Keyword, ArrayList<Category>> entry : Vocabulary.map.entrySet()) {
                for (Category c : entry.getValue()) {
                    DB_Matcher m = new DB_Matcher();
                    m.setId(match_id++);
                    m.setWord_id(entry.getKey().id);
                    m.setCategory_id(c.id);
                    db.dao_matcher().insertAll(m);
                }
            }
        }
    }

    public static void getAllKeywords(final AppDatabase db) {
        synchronized (mutex) {
            Vocabulary.nodes = new HashMap<>();

            List<DB_Keyword> db_keywords = db.dao_keyword().getAll();
            ArrayList<Keyword> app_keywords = new ArrayList<>();
            for (DB_Keyword db_k : db_keywords) {
                Keyword k = new Keyword();
                k.id = db_k.getId();
                k.name = db_k.getKeyword();
                app_keywords.add(k);
                Vocabulary.nodes.put(k.name, new Node());
            }
            Vocabulary.keywords = app_keywords;
            int last_keywords_id = 0;
            for (Keyword k : Vocabulary.keywords) {
                if (k.id > last_keywords_id) {
                    last_keywords_id = k.id;
                }
            }
            Keyword.last_id = last_keywords_id;

            List<DB_Category> db_categories = db.dao_category().getAll();
            ArrayList<Category> app_categories = new ArrayList<>();
            for (DB_Category db_c : db_categories) {
                Category c = new Category();
                c.id = db_c.getId();
                c.name = db_c.getCategory_name();
//                c.dateAdded = k.getDateAdded();
                app_categories.add(c);
            }
            Vocabulary.categories = app_categories;
            int last_categories_id = 0;
            for (Category c : Vocabulary.categories) {
                if (c.id > last_categories_id) {
                    last_categories_id = c.id;
                }
            }
            Category.last_id = last_categories_id;

            List<DB_Matcher> db_matches = db.dao_matcher().getAll();
            HashMap<Keyword, ArrayList<Category>> map = new HashMap<>();
            for (DB_Matcher m : db_matches) {
                Keyword k = Vocabulary.getWordById(m.getWord_id());
                if (!map.containsKey(k)) {
                    map.put(k, new ArrayList<>());
                }
                Category c = Vocabulary.getCategoryById(m.getCategory_id());
                map.get(k).add(c);
            }
            Vocabulary.map = map;
        }
    }

    public static void deleteAll(final AppDatabase db) {
        synchronized (mutex) {
            db.dao_keyword().deleteAll();
        }
    }
}
