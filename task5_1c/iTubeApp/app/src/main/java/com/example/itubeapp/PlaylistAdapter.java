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
