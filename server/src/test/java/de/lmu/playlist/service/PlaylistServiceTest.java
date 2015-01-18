package de.lmu.playlist.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import de.lmu.playlist.PlaylistModule;
import de.lmu.playlist.domain.dao.PlaylistDao;
import de.lmu.playlist.domain.entity.Playlist;

/**
 * @author martin
 */
@Test
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
        playlist1.setName("test.playlist");
        playlistService.addPlaylist(playlist1);

        Playlist playlist2 = new Playlist();
        playlist2.setName("random.playlist");
        playlist2.setGenre("rock");
        playlistService.addPlaylist(playlist2);

        Playlist playlist = playlistService.findPlaylist("test.playlist");
        Assert.assertEquals(playlist.getName(), "test.playlist");

        List<Playlist> playlists = playlistService.findPlaylists("rock", null);
        for (Playlist pl : playlists) {
            Assert.assertEquals(pl.getGenre(), "rock");
        }
    }

    @Test(expectedExceptions = MongoException.DuplicateKey.class)
    public void testUnique() {
        Playlist playlist = new Playlist();
        playlist.setName("unique.playlist");
        playlistService.addPlaylist(playlist);
        playlistService.addPlaylist(playlist);
    }

    @Test
    public void testUpdate() {
        Playlist playlist = new Playlist();
        playlist.setName("update.playlist");
        playlist.setGenre("weird");
        playlist.setLikes(2);
        playlistService.addPlaylist(playlist);

        playlistService.likePlaylist("update.playlist");
        Playlist updatedPlaylist = playlistService.findPlaylist("update.playlist");
        Assert.assertEquals(updatedPlaylist.getLikes(), 3);

        updatedPlaylist.setGenre("strange");
        playlistDao.update(updatedPlaylist);
        updatedPlaylist = playlistService.findPlaylist("update.playlist");
        Assert.assertEquals(updatedPlaylist.getGenre(), "strange");
    }

    @AfterClass
    public void after() {
        playlistDao.drop();
    }
}
