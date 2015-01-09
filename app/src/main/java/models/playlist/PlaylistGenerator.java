package models.playlist;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final int ERROR_GENRE_NOT_FOUND = 2;
    private static final String TAG = "main.java.models.playlist.PlaylistGenerator";

    private Listener listener;

    private Playlist playlist;
    private Song lastSong;

    private int similarArtistsCallsCount = 0;
    private int topTracksCallsCount = 0;
    private int genreCallsCount = 0;

    private LastfmMetadataWrapper lfm;

    /** artists data cache, feeded with responsedata of lastfm*/
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
        setUpLastArtist(songTaken);

        Song fittingSong = getFittingSong();
        if (fittingSong == null) {
            return null;
        }

        ArtistInfo artist = artistsPriority.get(fittingSong.getArtist());
        if (!artist.getSimilarArtistsCalled()) {
            artist.setSimilarArtistsCalled(true);
            similarArtistsCallsCount++;
            lfm.findSimilarArtists(artist.getName(), 10);
        }
        Log.v(TAG, artistsPriority.toString());
        return lastSong = fittingSong;
    }

    /** changes priority of last suggested artist */
    private void setUpLastArtist(boolean songTaken) {
        if (lastSong != null) {
            ArtistInfo lastArtist = artistsPriority.get(lastSong.getArtist());
            float priorityChange;
            if (songTaken) {
                priorityChange = 0.3f;
            } else {
                priorityChange = -0.3f;
            }
            if (lastArtist != null) {
                lastArtist.changePriority(priorityChange);
            }
        }
    }

    /** the heart of generation, select the fitting song out of the sorted DataModel*/
    //TODO: get in some randomness here, so artists with lower prio get a chance
    private Song getFittingSong()
    {
        TreeSet<ArtistInfo> artists = getBestArtists();
        Iterator<ArtistInfo> it = artists.descendingIterator();
        while (it.hasNext()) {
            ArtistInfo artist = it.next();
            for (SongInfo track : artist.getTopTracks()) {
                if (!track.isChecked()) {
                    track.setChecked(true);
                    return track;
                }
            }
        }
        return null;
    }

    @Override
    public void onSimilarArtistsCallback(String calledArtist, String[][] similarArtists) {
        similarArtistsCallsCount--;
        if (similarArtists == null || similarArtists.length == 0) {
            listener.callbackError(ERROR_NO_ARTIST_FOUND);
            return;
        }

        ArtistInfo calledArtistInfo = artistsPriority.get(calledArtist);
        if (calledArtist == null) {
            //TODO: kind of dirty, but for the first time, otherwise the app dies to easily
            //TODO: comes up, when artist name in request and response differs!
            //TODO: normally this case shouldn't come up, because of artistSearch? data should be consistent with lastfm data!
            //TODO: best way would be to tunnel the artistInfo of response to this position, so the entry can be changed!
            calledArtistInfo = new ArtistInfo("", calledArtist, 1);
            artistsPriority.put(calledArtist, calledArtistInfo);
        }
        for (String[] artistArray : similarArtists) {
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
                artist = new ArtistInfo(artistId, artistName, fitting);
                topTracksCallsCount++;
                lfm.findTopTracks(artistName, 5);
                artistsPriority.put(artistName, artist);
            }
            artist.setPriority(artist.getPriority() + fitting);
            calledArtistInfo.addSimilarArtist(artist);
        }
        notifyIfCallbacksFinished();
    }

    @Override
    public void onTopTracksCallback(String artistName, ArrayList<String> trackNames) {
        topTracksCallsCount--;
        if (trackNames == null || trackNames.size() == 0) {
            listener.callbackError(ERROR_NO_TRACK_FOUND);
            return;
        }

        ArrayList<SongInfo> songInfos = new ArrayList<>();
        for (String trackName : trackNames) {
            songInfos.add(new SongInfo(artistName, trackName));
        }

        ArtistInfo artist = artistsPriority.get(artistName);
        if (artist != null) {
            artist.setTopTracks(songInfos);
        } else {
            Log.e(TAG, "called get top tracks for artist, but no artist found! (maybe request artist is different to response artist?)");
        }
        notifyIfCallbacksFinished();
    }

    @Override
    public void onGenreArtistsCallback(String[][] artists) {
        genreCallsCount--;
        if (artists == null || artists.length == 0) {
            listener.callbackError(ERROR_GENRE_NOT_FOUND);
            return;
        }
        for (String[] artistArray : artists) {
            String artistId = artistArray[0];
            String artistName = artistArray[1];
            ArtistInfo artist = artistsPriority.get(artistName);
            if (artist == null) {
                artist = new ArtistInfo(artistId, artistName, 1);
                topTracksCallsCount++;
                lfm.findTopTracks(artistName, 5);
                artistsPriority.put(artistName, artist);
            }
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
        if (genre != null && !genre.isEmpty()) {
            playlist.setGenre(genre);
            genreCallsCount++;
            lfm.findGenreArtists(genre, 20);
        }
    }

    public void setInitSong(Song initSong) {
        if (initSong != null) {
            lastSong = initSong;
            String artistName = lastSong.getArtist();
            artistsPriority.put(artistName, new ArtistInfo(null, artistName, 3));
            if (artistName != null && !artistName.isEmpty()) {
                similarArtistsCallsCount++;
                lfm.findSimilarArtists(artistName, 5);
                topTracksCallsCount++;
                lfm.findTopTracks(artistName, 5);
            }
        }
    }

    private TreeSet<ArtistInfo> getBestArtists()
    {
        TreeSet<ArtistInfo> treeSet = new TreeSet<>(new Comparator<ArtistInfo>() {
            @Override
            public int compare(ArtistInfo lhs, ArtistInfo rhs) {

                return Float.compare(lhs.getPriority(), rhs.getPriority());
            }
        });
        treeSet.addAll(artistsPriority.values());
        return treeSet;
    }

    public int getRequestsCount()
    {
        return similarArtistsCallsCount + topTracksCallsCount + genreCallsCount;
    }

    private void notifyIfCallbacksFinished()
    {
        if (getRequestsCount() == 0) {
            listener.callbacksFinished();
        }
    }

    public interface Listener {
        public void callbacksFinished();
        public void callbackError(int errorStatus);
    }

    /** only for saving important call responses for artists */
    private class ArtistInfo {
        private String lstFmId;
        private String name;
        private ArrayList<SongInfo> topTracks = new ArrayList<>();
        private ArrayList<ArtistInfo> similarArtists;
        private boolean similarArtistsCalled = false;
        private float priority = 0;

        public ArtistInfo(String artistId, String artistName, float fitting) {
            name = artistName;
            lstFmId = artistId;
            priority = fitting;
        }

        public ArrayList<SongInfo> getTopTracks() {
            return topTracks;
        }

        public void setTopTracks(ArrayList<SongInfo> topTracks) {
            this.topTracks = topTracks;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getPriority() {
            return priority;
        }

        /** priority can't fall beneath 0, so he can be shown up again */
        public void setPriority(float priority) {
            this.priority = priority < 0 ? 0 : priority;
        }

        public void changePriority(float priorityChange) {
            setPriority( getPriority() + priorityChange );
            if (similarArtists != null) {
                for (ArtistInfo similarArtist : similarArtists) {
                    similarArtist.setPriority(similarArtist.getPriority() + (priorityChange * 0.5f));
                }
            }
        }

        public boolean getSimilarArtistsCalled() {
            return similarArtistsCalled;
        }

        public void setSimilarArtistsCalled(boolean similarArtistsCalled) {
            this.similarArtistsCalled = similarArtistsCalled;
        }

        public void addSimilarArtist(ArtistInfo artist) {
            if (similarArtists == null) {
                similarArtists = new ArrayList<>();
            }
            similarArtists.add(artist);
        }

        public ArrayList<ArtistInfo> getSimilarArtists() {
            return similarArtists;
        }

        @Override
        public String toString() {
            return "Prio: " + priority;
        }
    }

    private class SongInfo extends Song {
        private boolean isChecked = false;

        public SongInfo(String artistName, String trackName) {
            super(artistName, trackName);
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }
}
