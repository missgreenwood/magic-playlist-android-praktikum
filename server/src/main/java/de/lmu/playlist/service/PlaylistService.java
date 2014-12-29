package de.lmu.playlist.service;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import de.lmu.playlist.domain.entity.Playlist;

public interface PlaylistService {

    void addPlaylist(@NotNull Playlist playlist);

    @Nullable Iterable<Playlist> findPlaylist(@NotNull String author);
}
