package com.example.itubeapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;
@Dao
public interface VideoDao {
    @Insert
    void insertVideo(VideoEntity video);

    @Query("SELECT * FROM videos WHERE userId = :userId")
    List<VideoEntity> getVideosForUser(String userId);

    @Delete
    void deleteVideo(VideoEntity video);
}
