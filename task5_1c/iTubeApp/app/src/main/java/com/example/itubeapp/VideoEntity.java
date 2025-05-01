package com.example.itubeapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "videos")
public class VideoEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String url;
    public String userId;

    public VideoEntity(String title, String url, String userId) {
        this.title = title;
        this.url = url;
        this.userId = userId;
    }
}