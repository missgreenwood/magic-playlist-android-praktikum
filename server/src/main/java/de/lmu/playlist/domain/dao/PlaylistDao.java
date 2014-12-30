package de.lmu.playlist.domain.dao;

import de.lmu.playlist.domain.entity.Playlist;

public interface PlaylistDao {

    public void savePlaylist(Playlist playlist);

    public Iterable<Playlist> findPlaylist(String author);

    public void drop();
}

