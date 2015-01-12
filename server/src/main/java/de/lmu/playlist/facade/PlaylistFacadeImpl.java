package de.lmu.playlist.facade;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mongodb.MongoException;

import java.util.List;

import javax.ws.rs.QueryParam;

import de.lmu.playlist.PlaylistModule;
import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.domain.entity.SpotifyToken;
import de.lmu.playlist.service.PlaylistService;
import de.lmu.playlist.service.SpotifyService;

public class PlaylistFacadeImpl implements PlaylistFacade {

    private final PlaylistService playlistService;

    private final SpotifyService spotifyService;

    @Inject
    public PlaylistFacadeImpl(final PlaylistService playlistService, final SpotifyService spotifyService) {
        this.playlistService = playlistService;
        this.spotifyService = spotifyService;
    }

    @Override
    public String alive() {
        return "alive";
    }

    @Override
    public void addPlaylist(Playlist playlist) {
        try {
            playlistService.addPlaylist(playlist);
        } catch (MongoException.DuplicateKey duplicateKey) {
            // this exception occurs when we try to add a playlist with an already existing name.
            // our current strategy: ignore it! 
        }
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

    @Override
    public SpotifyToken getTokens(@QueryParam("auth_code") String authCode) {
        return spotifyService.obtainToken(authCode);
    }

    @Override
    public SpotifyToken refreshToken(@QueryParam("refresh_token") String refreshToken) {
        return spotifyService.refreshTokenPair(refreshToken);
    }
}
