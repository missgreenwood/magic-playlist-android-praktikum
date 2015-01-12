package models.playlist;

import android.content.Context;
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
    private static final float STANDARD_ARTIST_FITTING = 1;

    private Listener listener;

    private Playlist playlist;
    private Song lastSong;

    private int similarArtistsCallsCount = 0;
    private int topTracksCallsCount = 0;
    private int genreCallsCount = 0;

    private LastfmMetadataWrapper lfm;

    /** artists data cache, feeded with responsedata of lastfm*/
    private HashMap<String, ArtistInfo> artistsPriority = new HashMap<>();
    private int songCountLimit = 20;
    private boolean waiting;
    private boolean finished = false;

    public PlaylistGenerator(Listener listener) {
        this.listener = listener;
        lfm = new LastfmMetadataWrapper(this);
        playlist = new Playlist();
    }

    public void setContext (Context context) {
        lfm.setContext(context);
    }

    public void startGeneration() {
        if (finished || playlist.getSongsList().size() >= songCountLimit) {
            finished = true;
            listener.generationFinished(playlist);
        }
        Song song = getNextSong(false);
        if (song != null) {
            playlist.addSong(song);

        }
        if (getRequestsCount() == 0) {
            startGeneration();
        } else {
            waiting = true;
        }
    }

    /**
     * if old given song is not accepted, increase tryCounter else use new Song to get next song
     * when call finishes, onSimilarArtistsCallback is called
     */
    public Song getNextSong(boolean songTaken) {

        Song fittingSong = getFittingSong();
        if (fittingSong == null) {
            return null;
        }

        ArtistInfo artist = getArtistInfo(fittingSong.getArtist());
        setUpLastArtist(songTaken, artist);
        if (!artist.getSimilarArtistsCalled()) {
            artist.setSimilarArtistsCalled(true);
            similarArtistsCallsCount++;
            lfm.findSimilarArtists(artist.getName(), 10);
        }
        Log.v(TAG, artistsPriority.toString());
        return lastSong = fittingSong;
    }

    /** changes priority of last suggested artist */
    private void setUpLastArtist(boolean songTaken, ArtistInfo artist) {
        if (lastSong != null) {
            float priorityChange;
            if (songTaken) {
                priorityChange = 0.3f;
            } else {
                priorityChange = -0.3f;
            }
            artist.changePriority(priorityChange);
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
    public void onSimilarArtistsCallback(String calledArtist, String returnedArtist, String[][] similarArtists) {
        similarArtistsCallsCount--;
        if (finished) {
            return;
        }
        if (similarArtists == null || similarArtists.length == 0) {
            listener.callbackError(ERROR_NO_ARTIST_FOUND);
            return;
        }

        ArtistInfo calledArtistInfo = getArtistInfo(calledArtist, returnedArtist);
        for (String[] artistArray : similarArtists) {
            String artistName = artistArray[1];
            float fitting;
            try {
                fitting = Float.parseFloat(artistArray[2]);
            } catch (Exception e) {
                fitting = 0;
            }

            ArtistInfo artist = getArtistInfo(artistName);
            artist.setPriority(artist.getPriority() + fitting);
            calledArtistInfo.addSimilarArtist(artist);
        }
        callbackFinished();
    }

    @Override
    public void onTopTracksCallback(String calledArtist, String returnedArtist, ArrayList<String> trackNames) {
        topTracksCallsCount--;
        if (finished) {
            return;
        }
        if (trackNames == null || trackNames.size() == 0) {
            listener.callbackError(ERROR_NO_TRACK_FOUND);
            return;
        }

        Log.d(TAG, "calledArtist: \"" + calledArtist + "\" returnedArtist: \"" + returnedArtist + "\" list: " + artistsPriority);
        ArtistInfo calledArtistInfo = getArtistInfo(calledArtist, returnedArtist);
        Log.d(TAG, "PRIO: " + artistsPriority);

        if (calledArtistInfo.getTopTracks().size() == 0) {
            ArrayList<SongInfo> songInfos = new ArrayList<>();
            for (String trackName : trackNames) {
                songInfos.add(new SongInfo(returnedArtist, trackName));
            }
            calledArtistInfo.setTopTracks(songInfos);
        }

        callbackFinished();
    }

    @Override
    public void onGenreArtistsCallback(String[][] artists) {
        genreCallsCount--;
        if (finished) {
            return;
        }
        if (artists == null || artists.length == 0) {
            listener.callbackError(ERROR_GENRE_NOT_FOUND);
            return;
        }
        for (String[] artistArray : artists) {
            String artistName = artistArray[1];
            ArtistInfo artist = getArtistInfo(artistName);
            topTracksCallsCount++;
            lfm.findTopTracks(artistName, 5);
            artistsPriority.put(artistName, artist);
        }
    }

    private ArtistInfo getArtistInfo(String artistName) {
        return getArtistInfo(artistName, artistName);
    }

    /**never returns null, if no artist exists, a new one is created
     * @param requestedArtist artistName that was called in request
     * @param responseArtist artistName that came back from server response
     * */
    private synchronized ArtistInfo getArtistInfo (String requestedArtist, String responseArtist) {
        ArtistInfo artistInfo = artistsPriority.get(requestedArtist);
        if (artistInfo != null) {
            if (requestedArtist != responseArtist) {
                artistInfo.setName(responseArtist);
                artistsPriority.put(responseArtist, artistsPriority.remove(requestedArtist));
            }
        } else {
            artistInfo = artistsPriority.get(responseArtist);
            if (artistInfo == null) {
                artistInfo = new ArtistInfo(null, responseArtist, STANDARD_ARTIST_FITTING);
                artistsPriority.put(responseArtist, artistInfo);
                topTracksCallsCount++;
                lfm.findTopTracks(responseArtist, 5);
            }
        }
        return artistInfo;
    }

    public void addSongToPlaylist(Song song) {
        playlist.addSong(song);
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
            if (artistName != null && !artistName.isEmpty()) {
                artistsPriority.put(artistName, new ArtistInfo(null, artistName, 3));
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

    private void callbackFinished()
    {
        if (getRequestsCount() == 0 && waiting) {
            waiting = false;
            startGeneration();
        }
    }

    public void setSongCountLimit(int songCountLimit) {
        this.songCountLimit = songCountLimit;
    }

    public interface Listener {
        void callbackError(int errorStatus);

        void generationFinished(Playlist playlist);
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
            return "artistName: \"" + name + "\" Prio: " + priority;
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
