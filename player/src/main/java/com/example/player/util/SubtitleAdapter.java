package com.example.player.util;

import android.support.v7.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.player.R;
import com.example.player.db.SubtitleUrl;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

public class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.SubtitleViewHolder>{

    private List<SubtitleUrl> subtitleUrlList;
    private VideoPlayer player;
    private PlayerView playerView;
    private AlertDialog alertDialog;


    public SubtitleAdapter(List<SubtitleUrl> subtitleUrlList, VideoPlayer player, PlayerView playerView, AlertDialog alertDialog) {
        this.subtitleUrlList = subtitleUrlList;
        this.player = player;
        this.playerView = playerView;
        this.alertDialog = alertDialog;
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

    public class SubtitleViewHolder extends RecyclerView.ViewHolder{
        TextView subtitleName;

        public SubtitleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.subtitleName = itemView.findViewById(R.id.subtitle_text_view);
        }

        public void onBind(SubtitleUrl subtitleUrl){
            subtitleName.setText(subtitleUrl.getTitle());
            Log.d("title", "subtitleUrl.getTitle() >> " + subtitleUrl.getTitle());
            itemView.setOnClickListener(view -> {
                player.setSelectedSubtitle(subtitleUrl.getSubtitleUrl());
                playerView.getSubtitleView().setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            });
        }


    }
}
