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
import com.example.videoplayer.ICallback;
import com.example.videoplayer.IVideoPlayerService;
import com.example.videoplayer.VideoEntry;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private MetaViewModel metaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setId();
        bindService();
    }

    public void playPrev(){
        try {
            iVideoPlayerService.playPrev();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void playNext() {
        try {
            iVideoPlayerService.playNext();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            iVideoPlayerService.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            iVideoPlayerService.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
                .commit();

        metaViewModel = new ViewModelProvider(this).get(MetaViewModel.class);
    }

    IVideoPlayerService iVideoPlayerService;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            iVideoPlayerService = IVideoPlayerService.Stub.asInterface(service);

            try {
                iVideoPlayerService.registerCb(iCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                iVideoPlayerService.unRegisterCb(iCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            iVideoPlayerService = null;
        }
    };

    ICallback iCallback = new ICallback.Stub() {

        @Override
        public void getVideoName(String videoname) throws RemoteException {
            metaViewModel.getVideoName().postValue(videoname);
        }

        @Override
        public void getArtistName(String artist) throws RemoteException {
            metaViewModel.getArtistName().postValue(artist);
        }

        @Override
        public void getUri(String uri) throws RemoteException {
            metaViewModel.getUri().postValue(uri);
        }

        @Override
        public void getDuration(int duration) throws RemoteException {
            metaViewModel.getDuration().postValue(duration);
        }

        @Override
        public void getProgress(int progress) throws RemoteException {
            metaViewModel.getProgress().postValue(progress);
        }
    };

    public void playSelectedVideo(VideoEntry videoEntry){
        try {
            iVideoPlayerService.playSelectedVideo(videoEntry);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<VideoEntry> searchVideos(String query) throws RemoteException {
        List<VideoEntry> videoEntries = iVideoPlayerService.getVideos(query);

        Log.d(TAG, "onQueryTextSubmit: " + videoEntries.size());
        for (VideoEntry videoEntry : videoEntries) {
            Log.d(TAG, "getVideoName: " + videoEntry.getVideoName());
        }

        return videoEntries;
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportFragmentManager().popBackStack();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (iVideoPlayerService != null){
            try {
                iVideoPlayerService.unRegisterCb(iCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService();
        if (iVideoPlayerService != null){
            try {
                iVideoPlayerService.registerCb(iCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}