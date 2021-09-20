// ICallback.aidl
package com.example.videoplayer;

// Declare any non-default types here with import statements
import com.example.videoplayer.VideoEntry;

interface ICallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void getVideoName(String videoname);
    void getArtistName(String artist);
    void getUri(String uri);
    void getDuration(int duration);
    void getProgress(int progress);
}