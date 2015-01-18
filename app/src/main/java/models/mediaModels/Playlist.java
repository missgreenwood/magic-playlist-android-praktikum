package models.mediaModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import models.playlist.PlaylistsManager;

/**
 * Created by TheDaAndy on 27.12.2014.
 *
 */
public class Playlist implements Parcelable, Song.Listener {
    private static int uniqueId = 0;

    private transient ArrayList<Listener> observers;

    private ArrayList<Song> songs;
    private String name;
    private int likes = 0;
    private String genre;
    private transient boolean alreadyLiked;
    private transient boolean alreadyUploaded;
    private transient int id = -1;
    private boolean initializing = false;

    public Playlist() {
        name = "new Playlist " + uniqueId++;
        songs = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public Playlist(String name) {
        this();
        this.name = name;
    }

    public Playlist(String name, int id, String genre, int likes, boolean alreadyLiked, boolean alreadyUploaded) {
        this(name);
        this.id = id;
        this.genre = genre;
        this.likes = likes;
        this.alreadyLiked = alreadyLiked;
        this.alreadyUploaded = alreadyUploaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addObserver(Listener newObserver) {
        observers.add(newObserver);
    }

    public void removeObserver(Listener observer) {
        observers.remove(observer);
    }

    public void addSong(Song newSong) {
        songs.add(newSong);
        newSong.addObservers(this);
        PlaylistsManager.getInstance().playlistAddSong(this, newSong);
        notifyChange();
    }

    public void addSong(Song song, boolean fromDB) {
        songs.add(song);
        song.addObservers(this);
    }

    public void removeSong(Song song) {
        songs.remove(song);
        song.addObservers(null);
        PlaylistsManager.getInstance().playlistRemoveSong(this, song);
        notifyChange();
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
        notifyChange();
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
        PlaylistsManager.getInstance().savePlaylist(this);
        notifyChange();
    }

    public void resetInitialization()
    {
        if (songs != null) {
            initializing = true;
            for (Song song : getSongsList()) {
                // song.setMediaWrapper(null);
                song.setNotPlayable(false);
            }
            initializing = false;
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
        PlaylistsManager.getInstance().savePlaylist(this);
        notifyChange();
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
            boolean genresEqual;
            if (p.getGenre() == null) {
                genresEqual = getGenre() == null;
            } else {
                genresEqual = p.getGenre().equals(getGenre());
            }

            return p.getName().equals(getName()) &&
                    hasSameSongsList(p) &&
                    genresEqual;

        }
        return false;
    }

    private boolean hasSameSongsList(Playlist playlist) {
        ArrayList<Song> ownTracks = getSongsList(),
                        otherTracks = playlist.getSongsList();

        if (ownTracks.size() != otherTracks.size()) {
            return false;
        }
        for (Song song : otherTracks) {
            if (!ownTracks.contains(song)) {
                return false;
            }
        }
        for (Song song : ownTracks) {
            if (!otherTracks.contains(song)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAlreadyLiked() {
        return alreadyLiked;
    }

    public void setAlreadyLiked(boolean alreadyLiked) {
        this.alreadyLiked = alreadyLiked;
        PlaylistsManager.getInstance().savePlaylist(this);
        notifyChange();
    }

    public boolean isAlreadyUploaded() {
        return alreadyUploaded;
    }

    public void setAlreadyUploaded(boolean alreadyUploaded) {
        this.alreadyUploaded = alreadyUploaded;
        PlaylistsManager.getInstance().savePlaylist(this);
        notifyChange();
    }

    @Override
    public void onSongChange() {
        if (!initializing)
        notifyChange();
    }

    public interface Listener {
        void onPlaylistChange(Playlist playlist);
    }
}
