package com.example.videometadata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

public class MyReceiver extends BroadcastReceiver {
    private final static String TAG = "MyReceiver";
    private String currVideoName = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        Intent senIntent = new Intent("MyReceiver");
        if (!intent.getStringExtra("video name").equals(currVideoName)) {
            currVideoName = intent.getStringExtra("video name");
            senIntent.putExtra("video name", intent.getStringExtra("video name"));
            senIntent.putExtra("artist name", intent.getStringExtra("artist name"));
            senIntent.putExtra("duration", intent.getIntExtra("duration", 0));
            senIntent.putExtra("uri", intent.getStringExtra("uri"));
        }
        senIntent.putExtra("CURRENT_PROGRESS", intent.getIntExtra("CURRENT_PROGRESS", 0));
        context.sendBroadcast(senIntent);
    }
}