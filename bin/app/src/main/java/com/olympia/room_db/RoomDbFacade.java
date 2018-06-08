package com.olympia.room_db;

import com.olympia.Node;
import com.olympia.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomDbFacade {
    private static final Object mutex = new Object();
    public static void saveAllKeywords(final AppDatabase db) {
        synchronized (mutex) {
            db.keywordDao().deleteAll();
            for (String s : Vocabulary.keywords) {
                Keyword k = new Keyword();
                k.setKeyword(s);
                db.keywordDao().insertAll(k);
            }
        }
    }

    public static void getAllKeywords(final AppDatabase db) {
        synchronized (mutex) {
            List<Keyword> db_keywords = db.keywordDao().getAll();
            ArrayList<String> app_keywords = new ArrayList<>();
            Vocabulary.nodes = new HashMap<>();
            for (Keyword k : db_keywords) {
                String s = k.getKeyword();
                app_keywords.add(s);
                Vocabulary.nodes.put(s, new Node());
            }
            Vocabulary.keywords = app_keywords;
        }
    }

    public static void deleteAll(final AppDatabase db) {
        synchronized (mutex) {
            db.keywordDao().deleteAll();
        }
    }
}
