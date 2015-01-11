package de.lmu.playlist.service;

import java.util.List;

import de.lmu.playlist.domain.entity.Playlist;

public interface PlaylistService {

    /**
     * Persists a given playlist.
     *
     * @param playlist the playlist to persist. Must not be null.
     */
    void addPlaylist(Playlist playlist);

    /**
     * Retrieves the playlist with the given name.
     *
     * @param name the name of the playlist.
     * @return the playlist with that name or null if none was found.
     */
    Playlist findPlaylist(String name);

    /**
     * Returns all playlists of the given genre and/or containing the given artist
     *
     * @param genre  the genre of the playlist.
     * @param artist the artist that must be contained in the playlist.
     * @return the playlists or null if none satisfied the search conditions.
     */
    List<Playlist> findPlaylists(String genre, String artist);

    /**
     * Increments the Playlist's like count and persists that change.
     *
     * @param playlist the playlist to like.
     */
    void likePlaylist(Playlist playlist);

    /**
     * Proceed with caution.
     */
    void cleanDB();
}
