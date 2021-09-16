// IVideoPlayerService.aidl
package com.example.videoplayer;

// Declare any non-default types here with import statements
import com.example.videoplayer.VideoEntry;
import com.example.videoplayer.ICallback;

interface IVideoPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<VideoEntry> getVideos(in String query);
    void getSelectedVideo(in VideoEntry videoEntry);
    void registerCb(ICallback cb);
    void unRegisterCb(ICallback cb);
}