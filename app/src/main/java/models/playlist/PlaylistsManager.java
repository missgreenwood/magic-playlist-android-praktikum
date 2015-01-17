package models.playlist;

import android.content.Context;

import java.util.ArrayList;

import models.mediaModels.Playlist;
import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 29.12.2014.
 *
 */
public class PlaylistsManager implements Playlist.Listener {

    private ArrayList<Listener> observers;

    private final static PlaylistsManager instance = new PlaylistsManager();

    public static PlaylistsManager getInstance() {
        return instance;
    }

    private Playlist currentPlaylist;

    private ArrayList<Playlist> playlists;

    private PlaylistDatabaseHandler databaseHandler;
    private PlaylistFileHandler fileHandler = new PlaylistFileHandler();

    private PlaylistsManager() {
        observers = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    public void loadPlaylists()
    {
        ArrayList<Playlist> loadedPlaylists = databaseHandler.loadPlaylists();
//        if (loadedPlaylists == null || loadedPlaylists.size() == 0) {
//            loadedPlaylists = fileHandler.loadPlaylists();
//        }
        if (loadedPlaylists != null) {
            playlists.clear();
            for (Playlist playlist : loadedPlaylists) {
                playlist.addObserver(this);
                playlists.add(playlist);
            }
            notifyOnPlaylistsListChange();
        }
    }

    public void setCurrentPlaylist(Playlist playlist) {
        currentPlaylist = playlist;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void closeDb()
    {
        databaseHandler.close();
    }

    public void setContext(Context context) {
        databaseHandler = new PlaylistDatabaseHandler(context);
    }

    public boolean addPlaylist(Playlist playlist)
    {
        boolean success = true;
        if (!playlists.contains(playlist)) {
            playlist.addObserver(this);
            playlists.add(playlist);
            success = savePlaylist(playlist);
            notifyOnPlaylistsListChange();
        }
        return success;
    }

    public ArrayList<Playlist> getPlaylists()
    {
        return playlists;
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
        playlist.removeObserver(this);
        databaseHandler.destroyPlaylist(playlist);
        notifyOnPlaylistsListChange();
    }

    private void notifyOnPlaylistsListChange()
    {
        for (Listener observer : observers) {
            observer.onPlaylistsListChange();
        }
    }

    public void addObserver(Listener listener) {
        observers.add(listener);
    }

    public void removeObserver(Listener listener) {
        observers.remove(listener);
    }

    public boolean isPlaylistNameUnique(String playlistName) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                return false;
            }
        }
        return true;
    }

    //I don't use contains, because in equality of 2 playlists their songs has to be the same. In this case the name is important, because playlistName is unique!
    public boolean containsPlaylistName(Playlist playlist) {
        if (playlist == null) {
            return false;
        }
        for (Playlist ownPlaylist : playlists) {
            if (ownPlaylist.getName().equals(playlist.getName())) {
                return true;
            }
        }
        return false;
    }

    public Playlist getEqualPlaylist(Playlist playlist) {
        //indexOf works with equals function, so if we have a playlist equal this one, return it
        int index = playlists.indexOf(playlist);
        return index != -1 ? playlists.get(index) : null;
    }

    public void renamePlaylist(String oldName, String newName) {
        databaseHandler.changePlaylistName(oldName, newName);
    }

    @Override
    public void onPlaylistChange(Playlist playlist) {
        savePlaylist(playlist);
    }

    public boolean savePlaylist(Playlist playlist) {
        fileHandler.savePlaylist(playlist);
        return databaseHandler.savePlaylist(playlist);
    }

    public Song getSong(String artist, String songname) {
        return databaseHandler.getSong(artist, songname);
    }

    public Song getSong(int id) {
        return databaseHandler.getSong(id);
    }

    public interface Listener {
        void onPlaylistsListChange();
    }
}
