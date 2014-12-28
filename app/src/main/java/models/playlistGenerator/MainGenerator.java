package models.playlistGenerator;

import android.util.Log;

import models.metadatawrappers.LastFmListener;
import models.metadatawrappers.LastfmMetadataWrapper;
import models.mymodule.app.Song;

/**
 * Created by TheDaAndy on 27.12.2014.
 */
public class MainGenerator implements LastFmListener {

    private Playlist playlist;
    private Song lastSong;

    private LastfmMetadataWrapper lfm;

    public MainGenerator() {
        lfm = new LastfmMetadataWrapper(this);
        playlist = new Playlist();
    }

    public void getNextSong() {
        if (lastSong == null) {
            lastSong = new Song("Johnny Flynn", "Lost and Found");
        }
        lfm.findSimilarArtists(lastSong.getArtist(), 2);
    }

    @Override
    public void onSimilarArtistsCallback(String[] artists) {
        lfm.findTopTracks(artists[0], 5);
    }

    @Override
    public void onTopTracksCallback(Song[] tracks) {
        Song bestTrack = tracks[0];

        askForTrack(bestTrack.getArtist() + " - " + bestTrack.getSongname());
        Log.d("track:", bestTrack.getArtist() + " - " + bestTrack.getSongname());
    }

    private void askForTrack(String trackName) {

    }
}
