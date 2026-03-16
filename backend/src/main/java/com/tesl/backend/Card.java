package com.tesl.backend;

import java.util.List;

public class Card {
    private String id;
    private String imagePath;
    private List<String> colors;

    public Card(String id, String imagePath, List<String> colors) {
        this.id = id;
        this.imagePath = imagePath;
        this.colors = colors;
    }

    public String getId() { return id; }
    public String getImagePath() { return imagePath; }
    public List<String> getColors() { return colors; }
}