package models.playlist;

import java.util.ArrayList;

import controllers.mainFragments.MyPlaylistsFragment;
import models.mediaModels.Playlist;

/**
 * Created by TheDaAndy on 29.12.2014.
 */
public class PlaylistsManager {

    private ArrayList<Listener> observers;

    private static PlaylistsManager instance = new PlaylistsManager();

    public static PlaylistsManager getInstance() {
        return instance;
    }

    private ArrayList<Playlist> playlists;

    public PlaylistsManager() {
        observers = new ArrayList<>();
        playlists = PlaylistFileHandler.loadPlaylists();
    }

    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
        notifyOnPlaylistsListChange();
    }

    public ArrayList<Playlist> getPlaylists()
    {
        return playlists;
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
        playlist.destroy();
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

    public interface Listener {
        void onPlaylistsListChange();
    }
}
