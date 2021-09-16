package com.example.videometadata.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.videometadata.Fragment.MetadataFragment;
import com.example.videometadata.MetaViewModel;
import com.example.videometadata.R;
import com.example.videoplayer.IVideoPlayerService;
import com.example.videoplayer.VideoEntry;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private MetaViewModel metaViewModel;
    private IntentFilter intentFilter;
    private Intent intent;
    private static final String INTENT = "INTENT";
    private static final String AWAKE = "AWAKE";
    private static final String NOT_AWAKE = "NOT_AWAKE";
    private static final String PLAY_NEXT = "PLAY_NEXT";
    private static final String PLAY_PREV = "PLAY_PREV";
    private static final String PAUSE = "PAUSE";
    private static final String PLAY = "PLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setId();
        filterIntent();
        initIntent();
        bindService();

        Log.d(TAG, "INTENT: " + intent.getStringExtra(INTENT));
    }

    public void sendAwake() {
        intent.putExtra(INTENT, AWAKE);
        sendBroadcast(intent);
    }

    public void sendPrev() {
        intent.putExtra(INTENT, PLAY_PREV);
        sendBroadcast(intent);
    }

    public void sendNext() {
        intent.putExtra(INTENT, PLAY_NEXT);
        sendBroadcast(intent);
    }

    public void sendPause() {
        intent.putExtra(INTENT, PAUSE);
        sendBroadcast(intent);
    }

    public void sendPlay() {
        intent.putExtra(INTENT, PLAY);
        sendBroadcast(intent);
    }

    public void filterIntent() {
        intentFilter = new IntentFilter("MyReceiver");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void initIntent() {
        intent = new Intent("com.example.videometadata");
        intent.setComponent(new ComponentName("com.example.videoplayer", "com.example.videoplayer.BroadcastReceiver.MyReceiver"));
        intent.putExtra(INTENT, AWAKE);
        sendBroadcast(intent);
    }

    public void bindService() {
        Intent aidlIntent = new Intent("connect_to_aidl_service");
        aidlIntent.setComponent(new ComponentName("com.example.videoplayer", "com.example.videoplayer.Service.MyService"));
        bindService(aidlIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setId() {
        MetadataFragment metadataFragment = new MetadataFragment();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_container, metadataFragment)
                .addToBackStack("miniPlayerFragment")
                .commit();

        metaViewModel = new ViewModelProvider(this).get(MetaViewModel.class);
    }


    private String currVideoName = "";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getStringExtra("video name").equals(currVideoName)) {
                Log.d(TAG, "equals: ");
                currVideoName = intent.getStringExtra("video name");
                metaViewModel.getVideoName().setValue(intent.getStringExtra("video name"));
                metaViewModel.getArtistName().setValue(intent.getStringExtra("artist name"));
                metaViewModel.getUri().setValue(intent.getStringExtra("uri"));
                metaViewModel.getDuration().setValue(intent.getIntExtra("duration", 0));
            }
            metaViewModel.getProgress().setValue(intent.getIntExtra("CURRENT_PROGRESS", 0));
        }
    };


    IVideoPlayerService iVideoPlayerService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            iVideoPlayerService = IVideoPlayerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iVideoPlayerService = null;
        }
    };

    public List<VideoEntry> getSearchedVideos(String query) throws RemoteException {
        List<VideoEntry> videoEntries = iVideoPlayerService.getVideos(query);

        Log.d(TAG, "onQueryTextSubmit: " + videoEntries.size());
        for (VideoEntry videoEntry : videoEntries) {
            Log.d(TAG, "getVideoName: " + videoEntry.getVideoName());
        }

        return videoEntries;
    }


    @Override
    protected void onPause() {
        super.onPause();
        intent.putExtra(INTENT, NOT_AWAKE);
        sendBroadcast(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        intent.putExtra(INTENT, AWAKE);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(broadcastReceiver);
    }
}