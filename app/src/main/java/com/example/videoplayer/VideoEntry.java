package com.example.videoplayer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoEntry implements Parcelable {
    public static final Parcelable.Creator<VideoEntry> CREATOR = new Parcelable.Creator<VideoEntry>() {
        public VideoEntry createFromParcel(Parcel in) {
            return new VideoEntry(in);
        }

        public VideoEntry[] newArray(int size) {
            return new VideoEntry[size];
        }
    };

    private long id;
    private Uri uri;
    private String display_name;
    private String path;
    private String videoName;
    private String artistName;
    private String album;
    private int duration;
    private String volumeName;

    public VideoEntry(long id, Uri uri, String display_name, String path, String videoName, String artistName, String album, int duration, String volumeName) {
        this.id = id;
        this.uri = uri;
        this.display_name = display_name;
        this.path = path;
        this.videoName = videoName;
        this.artistName = artistName;
        this.album = album;
        this.duration = duration;
        this.volumeName = volumeName;
    }

    public String getVideoName() {
        return videoName;
    }

    public int getDuration() {
        return duration;
    }

    public Uri getUri() {
        return uri;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public long getId() {
        return id;
    }

    public VideoEntry(Parcel in){
        this.id = in.readLong();
        this.uri = Uri.parse(in.readString());
        this.display_name =  in.readString();
        this.path = in.readString();
        this.videoName = in.readString();
        this.artistName = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
        this.volumeName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.uri.toString());
        dest.writeString(this.display_name);
        dest.writeString(this.path);
        dest.writeString(this.videoName);
        dest.writeString( this.artistName);
        dest.writeString(this.album);
        dest.writeInt(this.duration);
        dest.writeString(this.volumeName);
    }

}
