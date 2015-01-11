package de.lmu.playlist.service;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import de.lmu.playlist.domain.dao.PlaylistDao;
import de.lmu.playlist.domain.entity.Playlist;
import de.lmu.playlist.domain.entity.Song;

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
    public Playlist findPlaylist(String name) {
        return playlistDao.findPlaylist(name);
    }

    @Override
    public List<Playlist> findPlaylists(String genre, String artist) {
        List<Playlist> playlists = playlistDao.findPlaylists(genre);

        if (artist != null && !artist.isEmpty()) {
            ArrayList<Playlist> list = new ArrayList<>();
            for (Playlist playlist : playlists) {
                if (playlist.getSongs() == null) {
                    continue;
                }
                for (Song song : playlist.getSongs()) {
                    if (song.getArtist().contains(artist)) {
                        list.add(playlist);
                        break;
                    }
                }
            }
            return list;
        }

        return playlists;
    }

    @Override
    public void likePlaylist(Playlist playlist) {
        playlist.setLikes(playlist.getLikes() + 1);
        playlistDao.update(playlist);
    }

    @Override
    public void cleanDB() {
        playlistDao.drop();
    }
}
