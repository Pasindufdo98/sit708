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