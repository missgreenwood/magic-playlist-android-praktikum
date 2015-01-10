package de.lmu.playlist.service;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import de.lmu.playlist.PlaylistModule;
import de.lmu.playlist.domain.dao.PlaylistDao;
import de.lmu.playlist.domain.entity.Playlist;

public class PlaylistServiceTest {

    private PlaylistService playlistService;
    private PlaylistDao playlistDao;

    @BeforeClass
    public void before() {
        Injector injector = Guice.createInjector(new PlaylistModule());
        playlistService = injector.getInstance(PlaylistService.class);
        playlistDao = injector.getInstance(PlaylistDao.class);

        playlistDao.drop();
    }

    @Test
    public void testAdd() {
        Playlist playlist1 = new Playlist();
        playlist1.setName("test.user");
        playlistService.addPlaylist(playlist1);

        Playlist playlist2 = new Playlist();
        playlist2.setName("random.user");
        playlist2.setGenre("rock");
        playlistService.addPlaylist(playlist2);

        Playlist playlist = playlistService.findPlaylist("test.user");
        Assert.assertEquals(playlist.getName(), "test.user");

        List<Playlist> playlists = playlistService.findPlaylists("rock", null);
        for (Playlist pl : playlists) {
            Assert.assertEquals(pl.getGenre(), "rock");
        }
    }

    @AfterClass
    public void after() {
        playlistDao.drop();
    }
}
