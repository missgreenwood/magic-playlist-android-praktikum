package models.playlist;

import android.content.Context;

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
    /** this error should never come alone, there will always be another error before... */
    private static final int ERROR_NO_NEXT_SONG_FOUND = 3;
    private static final float STANDARD_ARTIST_FITTING = 1;

    private Listener listener;

    private Playlist playlist;
    private SongInfo lastSong;

    private int similarArtistsCallsCount = 0;
    private int topTracksCallsCount = 0;
    private int genreCallsCount = 0;

    private LastfmMetadataWrapper lfm;

    /** artists data cache, feeded with responsedata of lastfm*/
    private HashMap<String, ArtistInfo> artistsPriority = new HashMap<>();
    private int songsCountLimit = 20;
    private boolean waiting;
    private boolean finished = false;




    public PlaylistGenerator(Listener listener, String genre, int songsCountLimit, Song initSong) {
        this.listener = listener;
        lfm = new LastfmMetadataWrapper(this);
        playlist = new Playlist();

        if (genre != null && !genre.isEmpty()) {
            playlist.setGenre(genre);
            genreCallsCount++;
            lfm.findGenreArtists(genre, 20);
        }

        this.songsCountLimit = songsCountLimit;

        if (initSong != null) {
            String artistName = initSong.getArtist();
            lastSong = new SongInfo(artistName, initSong.getSongname());
            if (artistName != null && !artistName.isEmpty()) {
                ArtistInfo newArtist = getArtistInfo(artistName);
                newArtist.setPriority(3);
                findSimilarArtists(newArtist);
                artistsPriority.put(artistName, newArtist);
            }
        }
    }



    public void setContext (Context context) {
        lfm.setContext(context);
    }

    public void startGeneration() {
        while (getRequestsCount() == 0) {
            if (finished || playlist.getSongsList().size() == songsCountLimit) {
                finishGeneration();
                return;
            }
            //if no next sound found, prevent endlessloop...
            if (lastSong == null) {
                listener.callbackError(ERROR_NO_NEXT_SONG_FOUND);
                finishGeneration();
                return;
            }
            SongInfo song = getNextSong(false);
            if (song != null) {
                playlist.addSong(new Song(song.getArtistName(), song.getTrackName()));
            }
        }
        waiting = true; //whenever all callbacks have been finished, and waiting is true, startGeneration is called again
    }

    public void finishGeneration()
    {
        if (!finished) {
            finished = true;
            listener.generationFinished(playlist);
        }
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public Playlist createNewPlaylist(String name) {
        return playlist = new Playlist(name);
    }





    /**
     * if old given song is not accepted, increase tryCounter else use new Song to get next song
     * when call finishes, onSimilarArtistsCallback is called
     */
    private SongInfo getNextSong(boolean songTaken) {

        if (lastSong != null) {
            setUpLastArtist(songTaken, getArtistInfo(lastSong.getArtistName()));
        }

        SongInfo fittingSong = getFittingSong();
        if (fittingSong != null) {
            ArtistInfo artist = getArtistInfo(fittingSong.getArtistName());
            findSimilarArtists(artist);
        }

        return lastSong = fittingSong;
    }

    /** changes priority of last suggested artist */
    private void setUpLastArtist(boolean songTaken, ArtistInfo artist) {
        float priorityChange;
        if (songTaken) {
            priorityChange = 0.3f;
        } else {
            priorityChange = -0.3f;
        }
        artist.changePriority(priorityChange);
    }

    /** the heart of generation, select the fitting song out of the sorted DataModel*/
    //TODO: get in some randomness here, so artists with lower prio get a chance
    private SongInfo getFittingSong()
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

    private int getRequestsCount()
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

    private synchronized ArtistInfo getArtistInfo(String artistName) {
        ArtistInfo artistInfo = artistsPriority.get(artistName);
        if (artistInfo == null) {
            artistInfo = new ArtistInfo(artistName, STANDARD_ARTIST_FITTING);
            artistsPriority.put(artistName, artistInfo);
            findTopTracks(artistInfo);
        }
        return artistInfo;
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
            artistInfo = getArtistInfo(requestedArtist);
        }
        return artistInfo;
    }

    private synchronized void findSimilarArtists(ArtistInfo artist) {
        if (!artist.getSimilarArtistsCalled()) {
            artist.setSimilarArtistsCalled(true);
            similarArtistsCallsCount++;
            lfm.findSimilarArtists(artist.getName(), 10);
        }
    }

    private synchronized void findTopTracks(ArtistInfo artist) {
        if (!artist.isTopTracksCalled()) {
            artist.setTopTracksCalled(true);
            topTracksCallsCount++;
            lfm.findTopTracks(artist.getName(), 5);
        }
    }




    @Override
    public synchronized void onSimilarArtistsCallback(String calledArtist, String returnedArtist, String[][] similarArtists) {
        similarArtistsCallsCount--;
        if (finished) {
            return;
        }
        if (similarArtists == null || similarArtists.length == 0) {
            listener.callbackError(ERROR_NO_ARTIST_FOUND);
            callbackFinished();
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
    public synchronized void onTopTracksCallback(String calledArtist, String returnedArtist, ArrayList<String> trackNames) {
        topTracksCallsCount--;
        if (finished) {
            return;
        }
        if (trackNames == null || trackNames.size() == 0) {
            listener.callbackError(ERROR_NO_TRACK_FOUND);
            callbackFinished();
            return;
        }

        ArtistInfo calledArtistInfo = getArtistInfo(calledArtist, returnedArtist);

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
    public synchronized void onGenreArtistsCallback(String[][] artists) {
        genreCallsCount--;
        if (finished) {
            return;
        }
        if (artists == null || artists.length == 0) {
            callbackFinished();
            listener.callbackError(ERROR_GENRE_NOT_FOUND);
            return;
        }
        for (String[] artistArray : artists) {
            String artistName = artistArray[1];
            getArtistInfo(artistName);
        }
        callbackFinished();
    }





    public interface Listener {
        void callbackError(int errorStatus);
        void generationFinished(Playlist playlist);
    }

    /** only for saving important call responses for artists */
    private class ArtistInfo {
        private String name;
        private ArrayList<SongInfo> topTracks = new ArrayList<>();
        private ArrayList<ArtistInfo> similarArtists;
        private boolean similarArtistsCalled = false;
        private float priority = 0;
        private boolean topTracksCalled;

        public ArtistInfo(String artistName, float fitting) {
            name = artistName;
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

        @Override
        public String toString() {
            return "artistName: \"" + name + "\" Prio: " + priority;
        }

        public void setTopTracksCalled(boolean topTracksCalled) {
            this.topTracksCalled = topTracksCalled;
        }

        public boolean isTopTracksCalled() {
            return topTracksCalled;
        }
    }

    private class SongInfo {
        private boolean isChecked = false;
        private String artistName;
        private String trackName;

        public SongInfo(String artistName, String trackName) {
            this.artistName = artistName;
            this.trackName = trackName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        public String getArtistName() {
            return artistName;
        }

        public String getTrackName() {
            return trackName;
        }
    }
}
