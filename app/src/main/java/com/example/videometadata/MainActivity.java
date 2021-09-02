package com.example.videometadata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private ImageView thumbnail;
    private TextView artist_name;
    private TextView video_name;
    private ProgressBar progressBar;
    private TextView progress_number;
    private TextView duration_number;
    private MetaViewModel metaViewModel;
    private IntentFilter intentFilter;
    private Intent intent;
    private ImageButton next_button;
    private ImageButton prev_button;
    private ImageButton pause_button;
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
        intentFilter = new IntentFilter("MyReceiver");
        registerReceiver(broadcastReceiver, intentFilter);
        intent = new Intent("com.example.videometadata");
        intent.setComponent(new ComponentName("com.example.videoplayer", "com.example.videoplayer.BroadcastReceiver.MyReceiver"));
        intent.putExtra(INTENT, AWAKE);
        Log.d(TAG, "INTENT: " + intent.getStringExtra(INTENT));
        sendBroadcast(intent);

        metaViewModel = new ViewModelProvider(this).get(MetaViewModel.class);

        metaViewModel.getVideoName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                handler.removeCallbacks(scrollText);
                video_name.setSelected(false);
                video_name.setText(s);
                intent.putExtra(INTENT, AWAKE);
                sendBroadcast(intent);
                handler.postDelayed(scrollText, 3000);
            }
        });

        metaViewModel.getArtistName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                artist_name.setText(s);
            }
        });

        metaViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setProgress(integer);
                progress_number.setText(convertTime(integer));
            }
        });

        metaViewModel.getDuration().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setMax(integer);
                duration_number.setText(convertTime(integer));
            }
        });

        metaViewModel.getUri().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Uri uri = Uri.parse(s);
                try {
                    thumbnail.setImageBitmap(getContentResolver().loadThumbnail(uri, new Size(400, 400), new CancellationSignal()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(INTENT, PLAY_PREV);
                sendBroadcast(intent);
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(INTENT, PLAY_NEXT);
                sendBroadcast(intent);
            }
        });

        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause_button.setSelected(!pause_button.isSelected());
                if (pause_button.isSelected()) {
                    intent.putExtra(INTENT, PAUSE);
                } else intent.putExtra(INTENT, PLAY);
                sendBroadcast(intent);

            }
        });
    }

    private void setId() {
        thumbnail = findViewById(R.id.video_thumnail);
        artist_name = findViewById(R.id.artist_name);
        video_name = findViewById(R.id.video_name);
        progressBar = findViewById(R.id.progress_bar);
        progress_number = findViewById(R.id.progress_number);
        duration_number = findViewById(R.id.duration_number);
        prev_button = findViewById(R.id.prev_button);
        next_button = findViewById(R.id.next_button);
        pause_button = findViewById(R.id.pause_button);
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    Runnable scrollText = new Runnable() {
        @Override
        public void run() {
            video_name.setSelected(true);
        }
    };

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

    public String convertTime(int time) {
        Log.d(TAG, "convertTime: ");
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) time),
                TimeUnit.MILLISECONDS.toSeconds((long) time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                time)));
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
        unregisterReceiver(broadcastReceiver);
    }
}