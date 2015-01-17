package de.lmu.playlist.facade;

import com.google.inject.Inject;
import com.mongodb.MongoException;

import java.util.List;

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
            throw new ConflictException();
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
    public List<Playlist> findSimilar(Playlist playlist) {
        return playlistService.findSimilarPlaylists(playlist, 5);
    }

    @Override
    public String likePlaylist(String name) {
        return String.valueOf(playlistService.likePlaylist(name).getLikes());
    }

    @Override
    public void clean() {
        playlistService.cleanDB();
    }

    @Override
    public SpotifyToken getTokens(String authCode) {
        return spotifyService.obtainTokenPair(authCode);
    }

    @Override
    public SpotifyToken refreshToken(String refreshToken) {
        return spotifyService.refreshTokenPair(refreshToken);
    }
}
