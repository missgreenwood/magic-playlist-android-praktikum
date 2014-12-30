package de.lmu.playlist.service;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import de.lmu.playlist.domain.entity.Playlist;

public interface PlaylistService {

    /**
     * Persists a given playlist.
     *
     * @param playlist the playlist to persist. Must not be null.
     */
    void addPlaylist(@NotNull Playlist playlist);

    /**
     * Returns all the playlists by the given author.
     *
     * @param author the author to retrieve playlists for.
     * @return the playlists by the author or null if none were found.
     */
    @Nullable Iterable<Playlist> findPlaylist(@NotNull String author);
}
