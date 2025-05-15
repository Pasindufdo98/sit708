
////////////////////////NewsApp//////////////

MainActivity.java

package com.example.newsapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

HomeFragment.java

package com.example.newsapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment {

    RecyclerView topStoriesRecycler, newsRecycler;
    List<NewsItem> topStoriesList, newsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        topStoriesRecycler = view.findViewById(R.id.topStoriesRecycler);
        newsRecycler = view.findViewById(R.id.newsRecycler);

        topStoriesList = getTopStories();
        newsList = getNewsList();

        NewsAdapter topStoriesAdapter = new NewsAdapter(topStoriesList, item -> openNewsDetail(item));
        NewsAdapter newsAdapter = new NewsAdapter(newsList, item -> openNewsDetail(item));

        topStoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topStoriesRecycler.setAdapter(topStoriesAdapter);

        newsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newsRecycler.setAdapter(newsAdapter);

        return view;
    }


    private List<NewsItem> getTopStories() {
        List<NewsItem> list = new ArrayList<>();
        list.add(new NewsItem("World News: Climate Update", "Today’s Climate News...", R.drawable.news1));
        list.add(new NewsItem("Technology: New robotic tools", "Latest tech developments...", R.drawable.news2));
        list.add(new NewsItem("Sports: Final Match Highlights", "Exciting game summary...", R.drawable.news3));
        return list;
    }

    private List<NewsItem> getNewsList() {
        List<NewsItem> list = new ArrayList<>();
        list.add(new NewsItem("Health Tips for Winter", "Stay healthy during cold...", R.drawable.news4));
        list.add(new NewsItem("Travel: Top Destinations 2025", "Best places to visit...", R.drawable.news5));
        list.add(new NewsItem("Finance: Stock Market News", "Markets see major changes...", R.drawable.news6));
        return list;
    }

    private void openNewsDetail(NewsItem item) {
        NewsDetailFragment fragment = NewsDetailFragment.newInstance(item);
        ((MainActivity) getActivity()).loadFragment(fragment);
    }
}

NewsAdapter.java

package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NewsItem item);
    }

    public NewsAdapter(List<NewsItem> newsList, OnItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.bind(newsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;

        NewsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.newsImage);
            titleView = itemView.findViewById(R.id.newsTitle);
        }

        void bind(final NewsItem item, final OnItemClickListener listener) {
            titleView.setText(item.getTitle());
            Glide.with(imageView.getContext()).load(item.getImageResId()).into(imageView);
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}

NewsDetailFragment.java

package com.example.newsapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class NewsDetailFragment extends Fragment {

    private static final String ARG_NEWS_ITEM = "news_item";
    private NewsItem newsItem;

    public static NewsDetailFragment newInstance(NewsItem item) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsItem = (NewsItem) getArguments().getSerializable(ARG_NEWS_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);

        ImageView imageView = view.findViewById(R.id.detailImage);
        TextView description = view.findViewById(R.id.detailDescription);
        RecyclerView relatedRecycler = view.findViewById(R.id.relatedRecycler);

        Glide.with(this).load(newsItem.getImageResId()).into(imageView);
        description.setText(newsItem.getDescription());

        List<NewsItem> relatedNews = new ArrayList<>();
        relatedNews.add(new NewsItem("Tech: New Technological Impact on Daily Life", "New inventions are changing how we work and live...", R.drawable.news2));
        relatedNews.add(new NewsItem("Health: Mental Wellness Tips", "Learn how to manage stress in 2025...", R.drawable.news4));
        relatedNews.add(new NewsItem("Travel: Backpacking in Europe", "Affordable travel routes for students...", R.drawable.news5));

        RelatedNewsAdapter adapter = new RelatedNewsAdapter(relatedNews, item -> {
            NewsDetailFragment fragment = NewsDetailFragment.newInstance(item);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        relatedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        relatedRecycler.setAdapter(adapter);

        return view;
    }
}

NewsItem.java

package com.example.newsapp;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private String title;
    private String description;
    private int imageResId;

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

RelatedNewsAdapter.java

package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RelatedNewsAdapter extends RecyclerView.Adapter<RelatedNewsAdapter.RelatedViewHolder> {

    private List<NewsItem> relatedList;
    private OnItemClickListener listener;

    
    public interface OnItemClickListener {
        void onItemClick(NewsItem item);
    }

    public RelatedNewsAdapter(List<NewsItem> relatedList, OnItemClickListener listener) {
        this.relatedList = relatedList;
        this.listener = listener;
    }

    @Override
    public RelatedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related_news, parent, false);
        return new RelatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelatedViewHolder holder, int position) {
        holder.bind(relatedList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return relatedList.size();
    }

    static class RelatedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;

        RelatedViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.relatedImage);
            titleView = itemView.findViewById(R.id.relatedTitle);
        }

        void bind(final NewsItem item, final OnItemClickListener listener) {
            titleView.setText(item.getTitle());
            Glide.with(imageView.getContext()).load(item.getImageResId()).into(imageView);
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}

xml files(UI)

activity_main.xml

<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />





fragment_home.xml

<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/topStoriesTitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Top Stories"
            android:textSize="18sp"
            android:textStyle="bold"
            android:paddingBottom="8dp" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/topStoriesRecycler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp" />

        <TextView
            android:id="@+id/newsTitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="News"
            android:textSize="18sp"
            android:textStyle="bold"
            android:paddingBottom="8dp" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/newsRecycler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

    </LinearLayout>
</ScrollView>


fragment_news_detail.xml

<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <ImageView
            android:id="@+id/detailImage"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:scaleType="centerCrop" />

        <TextView
            android:id="@+id/detailDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="News Description"
            android:textSize="16sp"
            android:paddingTop="12dp"
            android:paddingBottom="16dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Related News"
            android:textSize="18sp"
            android:textStyle="bold"
            android:paddingBottom="8dp" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/relatedRecycler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

    </LinearLayout>
</ScrollView>


item_news.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="8dp">

    <ImageView
        android:id="@+id/newsImage"
        android:layout_width="150dp"
        android:layout_height="100dp"
        android:scaleType="centerCrop" />

    <TextView
        android:id="@+id/newsTitle"
        android:layout_width="150dp"
        android:layout_height="wrap_content"
        android:text="News Title"
        android:textSize="14sp"
        android:maxLines="2"
        android:ellipsize="end"
        android:paddingTop="4dp" />
</LinearLayout>

item_related_news.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="8dp">

    <ImageView
        android:id="@+id/relatedImage"
        android:layout_width="100dp"
        android:layout_height="80dp"
        android:scaleType="centerCrop" />

    <TextView
        android:id="@+id/relatedTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:layout_marginStart="12dp"
        android:text="Related Title"
        android:textSize="14sp" />
</LinearLayout>



//////////////////////Itube APP///////////////

MainActivity.java

package com.example.itubeapp;

import android.content.Intent;
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
        playlist = db.videoDao().getAllVideos();

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
                    VideoEntity video = new VideoEntity("Video " + (playlist.size() + 1), embedUrl);
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


AppDatabase.java

package com.example.itubeapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {VideoEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract VideoDao videoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "video_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

LoginActvity.java

package com.example.itubeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

SignUpActivity.java

package com.example.itubeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText fullName, username, password, confirmPassword;
    Button createAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fullName = findViewById(R.id.inputFullName);
        username = findViewById(R.id.inputUsername);
        password = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.inputConfirmPassword);
        createAccountBtn = findViewById(R.id.btnCreateAccount);

        createAccountBtn.setOnClickListener(v -> {
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username.getText().toString());
            editor.putString("password", password.getText().toString());
            editor.apply();

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}


public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginBtn, signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.loginUsername);
        passwordInput = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.btnLogin);
        signupBtn = findViewById(R.id.btnSignup);

        loginBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            String savedUser = prefs.getString("username", "");
            String savedPass = prefs.getString("password", "");

            if (usernameInput.getText().toString().equals(savedUser)
                    && passwordInput.getText().toString().equals(savedPass)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}

PlaylistActivity.java

package com.example.itubeapp;

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

        List<VideoEntity> savedVideos = db.videoDao().getAllVideos();
        StringBuilder builder = new StringBuilder();

        for (VideoEntity video : savedVideos) {
            builder.append(video.url).append("\n\n");
        }

        playlistText.setText(builder.toString().isEmpty() ? "No videos saved yet." : builder.toString());
    }
}

PlaylistAdapter.java

package com.example.itubeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<VideoEntity> playlist;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(VideoEntity video);
    }

    public PlaylistAdapter(List<VideoEntity> playlist, OnItemClickListener listener) {
        this.playlist = playlist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoEntity video = playlist.get(position);
        holder.titleText.setText(video.title);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(video));
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.videoTitle);
        }
    }
}

videoDao.java

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

    @Query("SELECT * FROM videos")
    List<VideoEntity> getAllVideos();

    @Delete
    void deleteVideo(VideoEntity video);
}

VideoEntity.java

package com.example.itubeapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "videos")
public class VideoEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String url;

    public VideoEntity(String title, String url) {
        this.title = title;
        this.url = url;
    }
}


Xml files (UI)

activity_main.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="24dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <EditText
        android:id="@+id/inputUrl"
        android:hint="YouTube URL:"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textUri" />

    <Button
        android:id="@+id/playButton"
        android:text="PLAY"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp" />

    <Button
        android:id="@+id/addButton"
        android:text="ADD TO PLAYLIST"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp" />

    <Button
        android:id="@+id/myPlaylistButton"
        android:text="MY PLAYLIST"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp" />

    <WebView
        android:id="@+id/videoWebView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_marginTop="16dp"/>
</LinearLayout>


activity_login.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="24dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <EditText
        android:id="@+id/loginUsername"
        android:hint="username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="text" />

    <EditText
        android:id="@+id/loginPassword"
        android:hint="password"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />

    <Button
        android:id="@+id/btnLogin"
        android:text="LOGIN"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp" />

    <Button
        android:id="@+id/btnSignup"
        android:text="SIGN UP"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp" />
</LinearLayout>

activity_playlist.xml

    <?xml version="1.0" encoding="utf-8"?>
    <ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="24dp">

        <TextView
            android:id="@+id/playlistText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Loading playlist..."
            android:textSize="16sp"
            android:textColor="#000000" />
    </ScrollView>

activity_signup.xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="24dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <EditText
        android:id="@+id/inputFullName"
        android:hint="Full name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPersonName" />

    <EditText
        android:id="@+id/inputUsername"
        android:hint="Username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="text" />

    <EditText
        android:id="@+id/inputPassword"
        android:hint="Password"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />

    <EditText
        android:id="@+id/inputConfirmPassword"
        android:hint="Confirm password"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />

    <Button
        android:id="@+id/btnCreateAccount"
        android:text="Create account"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp" />
</LinearLayout>


item_video.xml

<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/videoTitle"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="12dp"
    android:text="Video Title"
    android:textSize="16sp" />