package models.mediaModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import models.playlist.PlaylistFileHandler;

/**
 * Created by TheDaAndy on 27.12.2014.
 */
public class Playlist implements Parcelable {
    //TODO save in DB
    private static int uniqueId = 0;

    private transient ArrayList<Listener> observers;

    private ArrayList<Song> songs;
    private String name;
    private int likes;
    private String genre;

    public Playlist() {
        name = "new Playlist " + uniqueId++;
        songs = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public Playlist(String name) {
        this();
        this.name = name;
    }

    public void addObserver(Listener newObserver) {
        observers.add(newObserver);
    }

    public void removeObserver(Listener observer) {
        observers.remove(observer);
    }

    public boolean addSong(Song newSong) {
        songs.add(newSong);
        boolean success = PlaylistFileHandler.savePlaylist(this);
        if (!success) {
            songs.remove(newSong);
            Log.e("ERROR", "could not save Playlist with new Song, write Rights?");
            return false;
        }
        notifyChange();
        return true;
    }

    public boolean removeSong(Song song) {
        songs.remove(song);
        boolean success = PlaylistFileHandler.savePlaylist(this);
        if (!success) {
            songs.add(song);
            Log.e("ERROR", "could not save Playlist with removed Song, write Rights?");
            return false;
        }
        notifyChange();
        return true;
    }

    public ArrayList<Song> getSongsList(boolean directAccess) {
        if (directAccess) {
            return songs;
        } else {
            return getSongsList();
        }
    }

    public ArrayList<Song> getSongsList() {
        return new ArrayList<>(songs);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        boolean success = PlaylistFileHandler.changePlaylistName(this.name, name);
        if (success) {
            this.name = name;
        } else {
            Log.e("ERROR", "could not change Playlistname, due being unable to rename Playlistfile. Rights?");
        }
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void resetInitialization()
    {
        if (songs != null) {
            for (Song song : getSongsList()) {
                // song.setMediaWrapper(null);
                song.setNotPlayable(false);
            }
            notifyChange();
        }
    }

    private void notifyChange() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).onPlaylistChange();
        }
    }

    public void destroy() {
        PlaylistFileHandler.destroy(getName());
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public interface Listener {
        void onPlaylistChange();
    }
}
