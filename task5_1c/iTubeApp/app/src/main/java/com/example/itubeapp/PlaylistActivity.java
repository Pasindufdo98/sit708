package com.example.itubeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextView playlistText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistText = findViewById(R.id.playlistText);
        db = AppDatabase.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String currentUserId = prefs.getString("username", "");

        List<VideoEntity> savedVideos = db.videoDao().getVideosForUser(currentUserId);
        StringBuilder builder = new StringBuilder();

        for (VideoEntity video : savedVideos) {
            builder.append(video.title).append("\n").append(video.url).append("\n\n");
        }

        playlistText.setText(builder.toString().isEmpty() ? "No videos saved yet." : builder.toString());
    }
}