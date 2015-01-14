package models.mediaModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import models.playlist.PlaylistFileHandler;
import models.playlist.PlaylistsManager;

/**
 * Created by TheDaAndy on 27.12.2014.
 */
public class Playlist implements Parcelable {
    //TODO save in DB
    private static int uniqueId = 0;

    private transient ArrayList<Listener> observers;

    private ArrayList<Song> songs;
    private String name;
    private int likes = 0;
    private String genre;
    private transient boolean alreadyLiked;
    private transient boolean alreadyUploaded;

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
        notifyChange();
        return true;
    }

    public boolean removeSong(Song song) {
        songs.remove(song);
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
        String oldName = this.name;
        this.name = name;
        PlaylistsManager.getInstance().renamePlaylist(oldName, name);
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
            observers.get(i).onPlaylistChange(this);
        }
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
    public void writeToParcel(Parcel dest, int flags) {}

    @Override
    public boolean equals(Object o) {
        if (o instanceof Playlist) {
            Playlist p = (Playlist) o;
            boolean genresEqual = true;
            if (p.getGenre() == null) {
                genresEqual = genre == null;
            } else {
                genresEqual = p.getGenre().equals(genre);
            }
            return p.getName().equals(getName()) &&
                    p.getSongsList().equals(getSongsList()) &&
                    genresEqual;

        }
        return false;
    }

    public boolean isAlreadyLiked() {
        return alreadyLiked;
    }

    public void setAlreadyLiked(boolean alreadyLiked) {
        this.alreadyLiked = alreadyLiked;
    }

    public boolean isAlreadyUploaded() {
        return alreadyUploaded;
    }

    public void setAlreadyUploaded(boolean alreadyUploaded) {
        this.alreadyUploaded = alreadyUploaded;
    }

    public interface Listener {
        void onPlaylistChange(Playlist playlist);
    }
}
