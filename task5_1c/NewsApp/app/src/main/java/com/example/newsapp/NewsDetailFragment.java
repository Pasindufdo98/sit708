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
        relatedNews.add(new NewsItem("Tech: AI's Impact on Daily Life", "AI is changing how we work and live...", R.drawable.news2));
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