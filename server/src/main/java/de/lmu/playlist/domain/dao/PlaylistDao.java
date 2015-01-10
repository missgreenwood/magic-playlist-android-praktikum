package de.lmu.playlist.domain.dao;

import java.util.List;

import de.lmu.playlist.domain.entity.Playlist;

public interface PlaylistDao {

    public void savePlaylist(Playlist playlist);

    public Playlist findPlaylist(String name);

    public List<Playlist> findPlaylists(String genre);

    public void drop();
}

