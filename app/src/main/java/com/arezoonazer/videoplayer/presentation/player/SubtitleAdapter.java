package com.arezoonazer.videoplayer.presentation.player;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arezoonazer.videoplayer.R;
import com.arezoonazer.videoplayer.data.database.Subtitle;
import com.arezoonazer.videoplayer.presentation.player.util.VideoPlayer;

import java.util.List;

public class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.SubtitleViewHolder> {

    private List<Subtitle> subtitleUrlList;
    private VideoPlayer player;

    public SubtitleAdapter(List<Subtitle> subtitleUrlList, VideoPlayer player) {
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

        public void onBind(Subtitle subtitleUrl) {
            subtitleName.setText(subtitleUrl.getTitle());
            Log.d("title", "subtitleUrl.getTitle() >> " + subtitleUrl.getTitle());
            itemView.setOnClickListener(view -> {
                player.setSelectedSubtitle(subtitleUrl);
            });
        }
    }
}
