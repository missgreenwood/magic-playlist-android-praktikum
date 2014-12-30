package de.lmu.playlist.facade;

import com.google.inject.Inject;
import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.service.PlaylistService;

public class PlaylistFacadeImpl implements PlaylistFacade {

    private final PlaylistService playlistService;

    @Inject
    public PlaylistFacadeImpl(final PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @Override
    public String alive() {
        return "alive";
    }

    @Override
    public void addPlaylist(Playlist playlist) {
        playlistService.addPlaylist(playlist);
    }

    @Override
    public Iterable<Playlist> findPlaylists(String author) {
        return playlistService.findPlaylist(author);
    }
}