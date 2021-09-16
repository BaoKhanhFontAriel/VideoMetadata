package com.example.videometadata.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.videometadata.Activity.MainActivity;
import com.example.videometadata.MetaViewModel;
import com.example.videometadata.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MetadataFragment extends Fragment {
    private ImageView thumbnail;
    private TextView artist_name;
    private TextView video_name;
    private ProgressBar progressBar;
    private TextView progress_number;
    private TextView duration_number;
    private ImageButton next_button;
    private ImageButton prev_button;
    private ImageButton pause_button;
    MetaViewModel metaViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.metadata_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thumbnail = view.findViewById(R.id.video_thumnail);
        artist_name = view.findViewById(R.id.artist_name);
        video_name = view.findViewById(R.id.video_name);
        progressBar = view.findViewById(R.id.progress_bar);
        progress_number = view.findViewById(R.id.progress_number);
        duration_number = view.findViewById(R.id.duration_number);
        prev_button = view.findViewById(R.id.prev_button);
        next_button = view.findViewById(R.id.next_button);
        pause_button = view.findViewById(R.id.pause_button);

        metaViewModel = new ViewModelProvider(requireActivity()).get(MetaViewModel.class);

        metaViewModel.getVideoName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                handler.removeCallbacks(scrollText);
                video_name.setSelected(false);
                video_name.setText(s);
                ((MainActivity) getActivity()).sendAwake();
                handler.postDelayed(scrollText, 3000);
            }
        });

        metaViewModel.getArtistName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                artist_name.setText(s);
            }
        });

        metaViewModel.getProgress().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setProgress(integer);
                progress_number.setText(convertTime(integer));
            }
        });

        metaViewModel.getDuration().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setMax(integer);
                duration_number.setText(convertTime(integer));
            }
        });

        metaViewModel.getUri().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Uri uri = Uri.parse(s);
                try {
                    thumbnail.setImageBitmap(getActivity().getContentResolver().loadThumbnail(
                            uri,
                            new Size(400, 400),
                            new CancellationSignal()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).sendPrev();
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).sendNext();
            }
        });

        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause_button.setSelected(!pause_button.isSelected());
                if (pause_button.isSelected()) {
                    ((MainActivity) getActivity()).sendPause();
                } else ((MainActivity) getActivity()).sendPlay();
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    Runnable scrollText = new Runnable() {
        @Override
        public void run() {
            video_name.setSelected(true);
        }
    };

    public String convertTime(int time) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) time),
                TimeUnit.MILLISECONDS.toSeconds((long) time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                time)));
    }
    private androidx.appcompat.widget.SearchView searchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            SearchFragment searchFragment = new SearchFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_container, searchFragment)
                    .commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
