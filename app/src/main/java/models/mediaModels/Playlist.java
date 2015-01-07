package models.mediaModels;

import android.util.Log;

import java.util.ArrayList;

import models.playlist.PlaylistFileHandler;
import models.playlist.PlaylistsManager;

/**
 * Created by TheDaAndy on 27.12.2014.
 */
public class Playlist {

    private static int uniqueId = 0;

    private ArrayList<IPlaylistListener> observers;

    private ArrayList<Song> songs;
    private String name;

    public Playlist () {
        name = "new Playlist " + uniqueId++;
        songs = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public Playlist (String name) {
        this();
        this.name = name;
    }

    public void addObserver(IPlaylistListener newObserver) {
        observers.add(newObserver);
    }

    public void removeObserver(IPlaylistListener observer) {
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

    public void removeSong (Song song) {
        songs.remove(song);
        notifyChange();
    }

    public ArrayList<Song> getSongsList() {
        return new ArrayList<>(songs);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        boolean success = PlaylistFileHandler.changePlaylistName(this.name, name);
        if (success) {
            this.name = name;
        } else {
            Log.e("ERROR", "could not change Playlistname, due being unable to rename Playlistfile. Rights?");
        }
    }

    private void notifyChange()
    {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).onPlaylistChange();
        }
    }
}
