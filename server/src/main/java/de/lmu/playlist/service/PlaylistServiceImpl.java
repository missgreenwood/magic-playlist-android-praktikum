package de.lmu.playlist.service;

import com.google.inject.Inject;
import de.lmu.playlist.domain.dao.PlaylistDao;
import de.lmu.playlist.domain.entity.Playlist;

public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistDao playlistDao;

    @Inject
    public PlaylistServiceImpl(final PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    @Override
    public void addPlaylist(Playlist playlist) {
        playlistDao.savePlaylist(playlist);
    }

    @Override
    public Iterable<Playlist> findPlaylist(String author) {
        return playlistDao.findPlaylist(author);
    }
}
