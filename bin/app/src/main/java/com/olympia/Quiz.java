package com.olympia;

import java.util.ArrayList;

public class Quiz {
    private Quiz() {} //* To prevent from instantiating
    public static ArrayList<Keyword> filteredWords = new ArrayList<>();
    public static int lastScore = 0;
    public static int overallScore = 0;
}
