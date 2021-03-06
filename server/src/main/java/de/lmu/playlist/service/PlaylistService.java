package de.lmu.playlist.service;

import java.util.List;

import de.lmu.playlist.domain.entity.Playlist;

/**
 * @author martin
 *         <p/>
 *         service for business logic of playlist related tasks.
 */
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
     * Returns playlists similar to the given reference playlist.
     *
     * @param referencePlaylist the playlist to find similar ones for
     * @param quantity          the quantity of similar lists
     * @return the most similar playlists.
     */
    List<Playlist> findSimilarPlaylists(Playlist referencePlaylist, int quantity);

    /**
     * Increments the Playlist's like count and persists that change.
     *
     * @param name the playlist to like.
     * @return the updated playlist
     */
    Playlist likePlaylist(String name);

    /**
     * Proceed with caution.
     */
    void cleanDB();
}
