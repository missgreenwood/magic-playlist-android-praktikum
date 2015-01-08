package models.playlist;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

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
    private static final String TAG = "main.java.models.playlist.PlaylistGenerator";

    private Listener listener;

    private Playlist playlist;
    private Song lastSong;
    private ArtistInfo lastArtist;
    private int tryCount = 0;

    private int similarArtistsCallsCount = 0;
    private int topTracksCallsCount = 0;

    private LastfmMetadataWrapper lfm;
    private String genre;

    private HashMap<String, ArtistInfo> artistsPriority = new HashMap<>();

    public PlaylistGenerator(Listener listener) {
        this.listener = listener;
        lfm = new LastfmMetadataWrapper(this);
        playlist = new Playlist();
    }

    /**
     * if old given song is not accepted, increase tryCounter else use new Song to get next song
     * when call finishes, onSimilarArtistsCallback is called
     */
    public Song getNextSong(boolean songTaken) {
        if (songTaken) {
            tryCount = 0;
        } else {
            tryCount++;
        }
        ArtistInfo artist = getBestArtist();
        if (!artist.getSimilarArtistsCalled()) {
            similarArtistsCallsCount++;
            artist.setSimilarArtistsCalled(true);
            lfm.findSimilarArtists(artist.getName(), 10);
        }
        return artist.getTopTracks().get(0);
    }

    @Override
    public void onSimilarArtistsCallback(String[][] artists) {
        similarArtistsCallsCount--;
        if (artists == null || artists.length == 0) {
            listener.nextSongError(ERROR_NO_ARTIST_FOUND);
            return;
        }

        for (String[] artistArray : artists) {
            String artistId = artistArray[0];
            String artistName = artistArray[1];
            float fitting;
            try {
                fitting = Float.parseFloat(artistArray[2]);
            } catch (Exception e) {
                fitting = 0;
            }
            ArtistInfo artist = artistsPriority.get(artistName);
            if (artist == null) {
                artist = new ArtistInfo(artistId, artistName);
                topTracksCallsCount++;
                lfm.findTopTracks(artistName, 5);
                artistsPriority.put(artistName, artist);
            }
            artist.setPriority(artist.getPriority() + fitting);
        }
    }

    @Override
    public void onTopTracksCallback(String artistName, ArrayList<Song> tracks) {
        topTracksCallsCount--;
        if (tracks == null || tracks.size() == 0) {
            listener.nextSongError(ERROR_NO_TRACK_FOUND);
            return;
        }

        ArtistInfo artist = artistsPriority.get(artistName);
        if (artist != null) {
            artist.setTopTracks(tracks);
        } else {
            Log.e(TAG, "called get top tracks for artist, but no artist found! (maybe request artist is different to response artist?)");
        }
    }

    @Override
    public void onTagArtistsCallback(String[][] artists) {
        for (String[] artistArray : artists) {
            String artistId = artistArray[0];
            String artistName = artistArray[1];
            ArtistInfo artist = artistsPriority.get(artistName);
            if (artist == null) {
                artist = new ArtistInfo(artistId, artistName);
                topTracksCallsCount++;
                lfm.findTopTracks(artistName, 5);
                artistsPriority.put(artistName, artist);
            }
            artist.setPriority(artist.getPriority() + 1);
        }
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

    public Playlist createNewPlaylist(String name) {
        return playlist = new Playlist(name);
    }

    public void setGenre(String genre) {
        playlist.setGenre(genre);
        lfm.findGenreArtists(genre, 20);
    }

    public void setInitSong(Song initSong) {
        this.lastSong = initSong;

    }

    private ArtistInfo getBestArtist()
    {
        TreeSet<ArtistInfo> treeSet = new TreeSet<>(new Comparator<ArtistInfo>() {
            @Override
            public int compare(ArtistInfo lhs, ArtistInfo rhs) {

                return Float.compare(lhs.getPriority(), rhs.getPriority());
            }
        });
        treeSet.addAll(artistsPriority.values());
        return treeSet.first();
    }

    public int getRequestsCount()
    {
        return similarArtistsCallsCount + topTracksCallsCount;
    }

    public interface Listener {
        public void nextSongFound(Song song);
        public void nextSongError(int errorStatus);
    }

    /** only for saving important call responses for artists */
    private class ArtistInfo {
        private String lstFmId;
        private String name;
        private ArrayList<Song> topTracks;
        private ArrayList<ArtistInfo> similarArtists;
        private boolean similarArtistsCalled = false;
        private float priority = 0;

        public ArtistInfo(String artistId, String artistName) {
            name = artistName;
            lstFmId = artistId;
        }

        public ArrayList<Song> getTopTracks() {
            return topTracks;
        }

        public void setTopTracks(ArrayList<Song> topTracks) {
            this.topTracks = topTracks;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLstFmId() {
            return lstFmId;
        }

        public void setLstFmId(String lstFmId) {
            this.lstFmId = lstFmId;
        }

        public float getPriority() {
            return priority;
        }

        public void setPriority(float priority) {
            this.priority = priority;
        }

        public boolean getSimilarArtistsCalled() {
            return similarArtistsCalled;
        }

        public void setSimilarArtistsCalled(boolean similarArtistsCalled) {
            this.similarArtistsCalled = similarArtistsCalled;
        }
    }
}
