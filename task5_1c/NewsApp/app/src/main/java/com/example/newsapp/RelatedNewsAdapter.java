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

    // Define interface
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
