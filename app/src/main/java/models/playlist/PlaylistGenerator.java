package models.playlist;

import java.util.ArrayList;

import models.mediaModels.Playlist;
import models.metadatawrappers.LastFmListener;
import models.metadatawrappers.LastfmMetadataWrapper;
import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 27.12.2014.
 */
public class PlaylistGenerator implements LastFmListener {

    private static final int ERROR_NO_ARTIST_FOUND = 0;
    private static final int ERROR_NO_TRACK_FOUND = 1;

    private Listener listener;

    private Playlist playlist;
    private Song lastSong;
    private int tryCount = 0;

    private LastfmMetadataWrapper lfm;

    public PlaylistGenerator(Listener listener) {
        this.listener = listener;
        lfm = new LastfmMetadataWrapper(this);
        playlist = new Playlist();
    }

    /**
     * if old given song is not accepted, increase tryCounter else use new Song to get next song
     * when call finishes, onSimilarArtistsCallback is called
     * @param song null if not accepted otherwise initial song or accepted song
     * */
    public void getNextSong(Song song) {
        if (song != null) {
            lastSong = song;
            tryCount = 0;
        } else {
            tryCount++;
        }
        lfm.findSimilarArtists(lastSong.getArtist(), tryCount + 1);
    }

    @Override
    public void onSimilarArtistsCallback(String[] artists) {
        int artistsCount = artists.length;
        if (artistsCount > 0) {
            if (artistsCount == tryCount) {
                lfm.findTopTracks(artists[tryCount-1], 5);
            } else if (artistsCount > 0) {
                lfm.findTopTracks(artists[artistsCount-1], 5);
            } else {
                listener.nextSongError(ERROR_NO_ARTIST_FOUND);
            }
        } else {
            listener.nextSongError(ERROR_NO_ARTIST_FOUND);
        }
    }

    @Override
    public void onTopTracksCallback(Song[] tracks) {
        ArrayList<Song> playlistSongs = playlist.getSongsList();
        for (Song track : tracks) {
            if (!playlistSongs.contains(track)) {
                listener.nextSongFound(track);
                return;
            }
        }
        listener.nextSongError(ERROR_NO_TRACK_FOUND);
    }

    public void addSongToPlaylist(Song song) {
        playlist.addSong(song);
    }

    public void savePlaylist() {
        PlaylistsManager.getInstance().addPlaylist(playlist);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void createNewPlaylist(String name) {
        playlist = new Playlist(name);
    }

    public interface Listener {
        public void nextSongFound(Song song);
        public void nextSongError(int errorStatus);
    }
}
