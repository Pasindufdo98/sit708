package com.example.itubeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private WebView videoWebView;
    private EditText inputUrl;
    private List<VideoEntity> playlist;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputUrl = findViewById(R.id.inputUrl);
        videoWebView = findViewById(R.id.videoWebView);
        Button playButton = findViewById(R.id.playButton);
        Button addButton = findViewById(R.id.addButton);
        Button myPlaylistButton = findViewById(R.id.myPlaylistButton);

        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        videoWebView.setWebViewClient(new WebViewClient());

        db = AppDatabase.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        currentUserId = prefs.getString("username", "");

        playlist = db.videoDao().getVideosForUser(currentUserId);

        playButton.setOnClickListener(v -> {
            String url = inputUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                String videoId = getVideoId(url);
                if (videoId != null) {
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    playVideo(embedUrl);
                }
            }
        });

        addButton.setOnClickListener(v -> {
            String url = inputUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                String videoId = getVideoId(url);
                if (videoId != null) {
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    VideoEntity video = new VideoEntity("Video " + (playlist.size() + 1), embedUrl, currentUserId);
                    db.videoDao().insertVideo(video);
                    playlist.add(video);
                    inputUrl.setText("");
                    playVideo(embedUrl);
                }
            }
        });

        myPlaylistButton.setOnClickListener(v -> {
            startActivity(new Intent(this, PlaylistActivity.class));
        });
    }

    private void playVideo(String embedUrl) {
        String iframeHtml = getIframeHtml(embedUrl);
        videoWebView.loadDataWithBaseURL(null, iframeHtml, "text/html", "utf-8", null);
    }

    private String getVideoId(String url) {
        if (url.contains("v=")) {
            return url.substring(url.indexOf("v=") + 2).split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        } else {
            return null;
        }
    }

    private String getIframeHtml(String videoUrl) {
        return "<html><body style='margin:0'>" +
                "<iframe width='100%' height='100%' src='" + videoUrl +
                "' frameborder='0' allowfullscreen></iframe></body></html>";
    }
}
