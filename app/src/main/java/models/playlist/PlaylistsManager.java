package models.playlist;

import java.util.ArrayList;

import models.mediaModels.Playlist;

/**
 * Created by TheDaAndy on 29.12.2014.
 */
public class PlaylistsManager {
    private static PlaylistsManager ourInstance = new PlaylistsManager();

    public static PlaylistsManager getInstance() {
        return ourInstance;
    }

    private ArrayList<Playlist> playlists;

    private PlaylistsManager() {
        playlists = new ArrayList<>();
        loadPlaylistsFromDB();
    }

    private void loadPlaylistsFromDB()
    {

    }

    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
        updateDB();
    }

    public void updateDB()
    {

    }
}
