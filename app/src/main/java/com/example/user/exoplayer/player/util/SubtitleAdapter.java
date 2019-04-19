package com.example.user.exoplayer.player.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.exoplayer.R;
import com.example.user.exoplayer.player.db.SubtitleUrl;

import java.util.List;

public class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.SubtitleViewHolder> {

    private List<SubtitleUrl> subtitleUrlList;
    private VideoPlayer player;

    public SubtitleAdapter(List<SubtitleUrl> subtitleUrlList, VideoPlayer player) {
        this.subtitleUrlList = subtitleUrlList;
        this.player = player;
    }

    @NonNull
    @Override
    public SubtitleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SubtitleViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subtitle_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubtitleViewHolder subtitleViewHolder, int i) {
        subtitleViewHolder.onBind(subtitleUrlList.get(i));
    }

    @Override
    public int getItemCount() {
        return subtitleUrlList.size();
    }

    public class SubtitleViewHolder extends RecyclerView.ViewHolder {
        TextView subtitleName;

        public SubtitleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.subtitleName = itemView.findViewById(R.id.subtitle_text_view);
        }

        public void onBind(SubtitleUrl subtitleUrl) {
            subtitleName.setText(subtitleUrl.getTitle());
            Log.d("title", "subtitleUrl.getTitle() >> " + subtitleUrl.getTitle());
            itemView.setOnClickListener(view -> {
                player.setSelectedSubtitle(subtitleUrl.getSubtitleUrl());
            });
        }
    }
}
