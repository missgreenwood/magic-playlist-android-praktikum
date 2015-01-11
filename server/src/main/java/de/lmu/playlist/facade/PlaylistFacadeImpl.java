package de.lmu.playlist.facade;

import com.google.inject.Inject;

import java.util.List;

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
    public Playlist findPlaylist(String name) {
        return playlistService.findPlaylist(name);
    }

    @Override
    public List<Playlist> findPlaylists(String genre, String artist) {
        return playlistService.findPlaylists(genre, artist);
    }

    @Override
    public void likePlaylist(Playlist playlist) {
        playlistService.likePlaylist(playlist);
    }

    @Override
    public void clean() {
        playlistService.cleanDB();
    }
}
