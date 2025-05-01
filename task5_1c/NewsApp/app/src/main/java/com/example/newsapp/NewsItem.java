package com.example.newsapp;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private String title;
    private String description;
    private int imageResId; // use drawable resource id instead of URL string

    public NewsItem(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}

