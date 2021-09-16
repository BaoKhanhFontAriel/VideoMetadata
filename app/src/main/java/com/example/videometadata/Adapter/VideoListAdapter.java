package com.example.videometadata.Adapter;

import android.content.Context;

import com.example.videometadata.R;
import com.example.videoplayer.VideoEntry;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends BaseListAdapter{

    private List<VideoEntry> videoEntryArrayList;

    public VideoListAdapter(List<VideoEntry> videoEntries, BaseListAdapter.IEntryClicked callback, Context context) {
        super(callback, context);
        this.videoEntryArrayList = videoEntries;
        setLayoutId(R.layout.video_entry_ui);
        setTitleId(R.id.video_name);
        setDetailId(R.id.artist_and_duration);
        setThumbnailId(R.id.thumbnail);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        VideoEntry videoEntry = videoEntryArrayList.get(position);

        holder.title.setText(videoEntry.getVideoName());
        String artistAndDuration = videoEntry.getArtistName() + " - " + convertTime(videoEntry.getDuration());
        holder.detail.setText(artistAndDuration);
        holder.thumbnail.setImageBitmap(getThumbnail(videoEntry.getUri()));
    }

    @Override
    public int getItemCount() {
        return videoEntryArrayList.size();
    }
}
