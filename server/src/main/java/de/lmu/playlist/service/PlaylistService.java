package de.lmu.playlist.service;

import de.lmu.playlist.domain.entity.Playlist;

public interface PlaylistService {

    /**
     * Persists a given playlist.
     *
     * @param playlist the playlist to persist. Must not be null.
     */
    void addPlaylist(Playlist playlist);

    /**
     * Returns the playlist by the given name.
     *
     * @param name the name of the playlist.
     * @return the playlist with that name or null if none was found.
     */
    Playlist findPlaylist(String name);
}
