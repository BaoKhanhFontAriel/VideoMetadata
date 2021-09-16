package com.example.videometadata.Fragment;

import android.app.SearchManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videometadata.Activity.MainActivity;
import com.example.videometadata.Adapter.BaseListAdapter;
import com.example.videometadata.Adapter.VideoListAdapter;
import com.example.videometadata.R;
import com.example.videoplayer.VideoEntry;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.searchable_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private androidx.appcompat.widget.SearchView searchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);

        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);// set drawable icon
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search_in_search_activity);
        searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.onActionViewExpanded();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<VideoEntry> videoEntries = new ArrayList<>();
                try {
                    videoEntries = ((MainActivity) getActivity()).getSearchedVideos(query);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                VideoListAdapter videoListAdapter = new VideoListAdapter(videoEntries, callback, getContext());
                recyclerView.setAdapter(videoListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    BaseListAdapter.IEntryClicked callback = new BaseListAdapter.IEntryClicked() {
        @Override
        public void onItemClicked(int position) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

        // 16908332 is the id of back button
        if (item.getItemId() == 16908332){
            Log.d(TAG, "home: ");
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            MetadataFragment metadataFragment = new MetadataFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_container, metadataFragment)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
