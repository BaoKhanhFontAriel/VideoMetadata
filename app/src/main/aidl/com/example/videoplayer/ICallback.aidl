// ICallback.aidl
package com.example.videoplayer;

// Declare any non-default types here with import statements
import com.example.videoplayer.VideoEntry;

interface ICallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    VideoEntry getSelectedVideo();
}