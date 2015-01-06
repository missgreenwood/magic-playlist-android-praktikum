package models.playlist;

import java.util.ArrayList;

import models.mediaModels.Playlist;

/**
 * Created by TheDaAndy on 29.12.2014.
 */
public class PlaylistsManager {
    private static PlaylistsManager ourInstance = new PlaylistsManager();
    private int uniquePlaylistId = 0;

    public static PlaylistsManager getInstance() {
        return ourInstance;
    }

    private ArrayList<Playlist> playlists;

    public PlaylistsManager() {
        playlists = new ArrayList<>();
        loadPlaylistsFromDB();
    }

    private void loadPlaylistsFromDB()
    {
        // TODO: implement
    }

    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }

    public ArrayList<Playlist> getPlaylists()
    {
        return playlists;
    }

    public int getUniquePlaylistId() {
        return uniquePlaylistId++;
    }
}
