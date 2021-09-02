package com.example.videometadata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MetaViewModel extends ViewModel {
    private MutableLiveData<String> videoName = new MutableLiveData<>();
    private MutableLiveData<String> artistName = new MutableLiveData<>();
    private MutableLiveData<String> uri = new MutableLiveData<>();
    private MutableLiveData<Integer> duration = new MutableLiveData<>();
    private MutableLiveData<Integer> progress = new MutableLiveData<>();

    public MutableLiveData<String> getVideoName() {
        return videoName;
    }

    public MutableLiveData<String> getArtistName() {
        return artistName;
    }

    public MutableLiveData<String> getUri() {
        return uri;
    }

    public MutableLiveData<Integer> getDuration() {
        return duration;
    }

    public MutableLiveData<Integer> getProgress() {
        return progress;
    }
}
