package de.lmu.playlist.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.lmu.playlist.PlaylistModule;
import de.lmu.playlist.domain.dao.PlaylistDao;
import de.lmu.playlist.domain.entity.Playlist;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PlaylistServiceTest {

    private PlaylistService playlistService;
    private PlaylistDao playlistDao;

    @BeforeClass
    public void before() {
        Injector injector = Guice.createInjector(new PlaylistModule());
        playlistService = injector.getInstance(PlaylistService.class);
        playlistDao= injector.getInstance(PlaylistDao.class);

        playlistDao.drop();
    }

    @Test
    public void testAdd() {
        playlistService.addPlaylist(new Playlist("test.user"));
        playlistService.addPlaylist(new Playlist("test.user"));
        playlistService.addPlaylist(new Playlist("random.user"));
        Iterable<Playlist> playlists = playlistService.findPlaylist("test.user");
        Assert.assertNotNull(playlists);
        for (Playlist playlist : playlists) {
            Assert.assertEquals(playlist.getAuthor(), "test.user");
        }
    }

    @AfterClass
    public void after() {
        playlistDao.drop();
    }
}
