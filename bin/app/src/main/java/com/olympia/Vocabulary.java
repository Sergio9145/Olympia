package com.olympia;

import com.olympia.oxford_api.model.Sense;

import java.util.ArrayList;

public class Vocabulary {
    public ArrayList<String> vocabulary;
    public ArrayList<Definition> definitions;
    public ArrayList<Sense> senses;

    private static Vocabulary instance;

    private Vocabulary(){
        vocabulary = new ArrayList<>();
        definitions = new ArrayList<>();
        senses = new ArrayList<>();
    }

    public static synchronized Vocabulary getInstance(){
        if(instance == null){
            instance = new Vocabulary();
        }
        return instance;
    }
}
