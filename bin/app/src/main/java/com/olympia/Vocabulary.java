package com.olympia;

import java.util.ArrayList;

public class Vocabulary {
    public static ArrayList<Node> nodes = new ArrayList<>();
    public static ArrayList<String> keywords = new ArrayList<>();

    private Vocabulary(){}

    public static Node getNode(String word) {
        int currentPosition = -1;
        for (int i = 0; i < keywords.size(); i++) {
            if (keywords.get(i).equalsIgnoreCase(word)) {
                currentPosition = i;
            }
        }
        return nodes.get(currentPosition);
    }
}
