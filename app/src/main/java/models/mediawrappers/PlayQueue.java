package models.mediawrappers;

import android.content.Context;
import android.util.Log;

import models.mediaModels.Playlist;
import models.mediaModels.Song;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by charlotte on 06.12.14.
 * @author charlotte
 *
 * Represents the play queue comprised of a collection of songs that can be played one after another,
 * randomly or chosen individually.
 */
public class PlayQueue {


    public static final String TAG = "main.java.models.mediawrappers.PlayQueue";

    //TODO: muss in FileStreamingMediaService
    public static final String SONG_AVAILABLE = "com.example.song_available";
    public static final String SONG_NOT_AVAILABLE = "com.example.song_not_available";
    private final static Object lockObject = new Object();
    private static final int STATE_WAITING = 0;
    private static final int STATE_ALREADY_PlAYING = 1;
    private static final int IDLE = 2;
    public static String SONG_ID = "com.example.song_id";
    //TODO: das ist nur vorläufig:
    private Song currentSong;
    private Context context;
    private int counter;
    private ArrayList<Song> songs;
    private int state = 2;

    private static final PlayQueue instance = new PlayQueue();

    //TODO: should be read from a preferences file or something
    private ArrayList<String> mediaWrappers;
    /**
     * This class should be used to create a playlist/queue with a list of songs.
     * The mediawrapper type that should be used has to be specified for every song (see Song class).
     */
    public PlayQueue() {

        if (PlayQueue.getInstance() != null) {
            Log.e("ERROR", "dont initialize PlayQueue! It's a wild singleton!");
        }

        mediaWrappers = new ArrayList<String>();
        mediaWrappers.add(Song.MEDIA_WRAPPER_LOCAL_FILE);
        mediaWrappers.add(Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD);
        mediaWrappers.add(Song.MEDIA_WRAPPER_SPOTIFY);


        //TODO: this should be done somewhere else!

        setState(IDLE);
    }

    public static PlayQueue getInstance()
    {
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void importPlaylist(Playlist playlist) {
        songs = playlist.getSongsList();
        initializePlaylist(true);
        Song currentSong = getCurrentSong();
        if (currentSong != null) {
            currentSong.getMediaWrapper().stopPlayer();
        }
        counter = 0;
        setCurrentSong(songs.get(counter));
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        Log.v(TAG, "set state " + getState() + " to " + state);

        this.state = state;
    }

    public void unsetAllWrappers() {
        for (Song song : songs) {

            song.setMediaWrapper(null);
        }

    }


    public Song getSongForID(int songID) {

        for (Song song : songs) {

            if (song.getSongID() == songID)
                return song;
        }

        return null;
    }

    //TODO: should rather be in class Song?
    public String getNextType(Song song) {

        ListIterator<String> it = mediaWrappers.listIterator();

        while (it.hasNext()) {

            String mediaWrapperType = it.next();
            if (mediaWrapperType.equals(song.getMediaWrapperType()) && it.hasNext())
                return it.next();
        }

        return null;

    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }




    /**
     * This method should be used by other classes to play the songs previously added
     * to the song list of the play queue; will overwrite media wrappers.
     * @param overwrite Will overwrite existing media wrappers if true
     * @deprecated use importPlaylist function, this does all you want...
     */
    public void playSongs(boolean overwrite) {


        Log.v(TAG, "play songs called");
        initializePlaylist(overwrite);
        playCurrentSong();
    }

    private void playCurrentSong() {

        Log.v(TAG, "play current song, counter is " + counter);

        if (counter < songs.size() && counter >= 0) {

            setCurrentSong(songs.get(counter));

            if (currentSong.getMediaWrapper() != null) {
                String playpath = currentSong.getMediaWrapper().getPlayPath();
             //   Log.d(TAG, "playpath: " + playpath);
                if ((playpath != null) && (!playpath.equals(""))) {

                 //   Log.d(TAG, "now we can play the current song");
                    setState(STATE_ALREADY_PlAYING);
                    currentSong.getMediaWrapper().play();
                }
            } else setState(STATE_WAITING);

        }
    }


    private void initializeSong(Song song) {


        //  ArrayList<Song> songsTemp = new ArrayList<Song>();
        //    songsTemp.add(getCurrentSong());

        //  if (song.getMediaWrapper() == null) {


        AbstractMediaWrapper abstractMediaWrapper = null;

        if (!mediaWrappers.contains(song.getMediaWrapperType()))
            song.setMediaWrapperType(mediaWrappers.get(0));

        String mediaWrapperType = song.getMediaWrapperType();


        //TODO: in Methode
        if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_LOCAL_FILE)) {
            abstractMediaWrapper = new LocalFileStreamingMediaWrapper(context, song);

        } else if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD)) {
            abstractMediaWrapper = new SoundCloudStreamingMediaWrapper(context, song);

        } else if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_SPOTIFY)) {

            abstractMediaWrapper = new SpotifyMediaWrapper(context, song);
            }


        song.setMediaWrapper(abstractMediaWrapper);

        //  }

        if (song.getMediaWrapper() != null)
            song.getMediaWrapper().lookForSong();


    }

    /**
     * @param overwrite Will overwrite existing media wrappers if true
     */
    private void initializePlaylist(boolean overwrite) {


        for (Song song : songs) {

            Log.d(TAG, "initialize song: " + song.toString());

            if (overwrite || (song.getMediaWrapper() == null))
                initializeSong(song);

        }


    }


    /**
     * Can be used by the GUI to go to the next track (forward button).
     *
     */
    public void nextTrack() {

        counter++;
        jumpToTrack(counter);

    }


    /**
     * Can be used by the GUI to go to the next track (back button).
     *
     */
    public void beforeTrack() {

        synchronized (lockObject) {
            counter--;
            if (counter < 0)
                counter = songs.size() - 1;
        }
        jumpToTrack(counter);

    }


    /**
     * Can be used by the GUI to play a random track.
     *
     */
    public void randomTrack() {

        Random rand = new Random();
        counter = rand.nextInt(songs.size());
        jumpToTrack(counter);

    }


    /**
     * Can be used by the GUI to jump to a specific track.
     *
     * @param index track index
     */
    public void jumpToTrack(int index) {

        Log.v(TAG, "is jumping to " + index);

        Song currentSong = getCurrentSong();

        if (currentSong != null) {
            currentSong.getMediaWrapper().stopPlayer();
        }

        counter = index;

        setState(IDLE);
        //playSongs();
        playCurrentSong();


    }

    /**
     * Can be used by the GUI when pause button was pressed.
     */
    public void pausePlayer() {
        Song currentSong = getCurrentSong();

        if (currentSong != null) {
            currentSong.getMediaWrapper().pausePlayer();
        }
    }

    /**
     * Can be used by the GUI when resume button was pressed.
     */
    public void resumePlayer() {

        if (getState() == STATE_ALREADY_PlAYING)
            getCurrentSong().getMediaWrapper().resumePlayer();

        else playCurrentSong();
    }

    /**
     * Tries setting another wrapper if the current wrapper cannot play
     * the song. Not yet implemented.
     *
     * @return song
     */
    private boolean trySettingNextWrapper(Song song) {


        String type = getNextType(song);


        if (type == null)
            return false;


        song.setMediaWrapper(null);
        song.setMediaWrapperType(type);

        Log.d(TAG, "new mediawrapper type: " + type);

        return true;

    }


    public void onTrackFinished() {
        if (counter < songs.size())
            nextTrack();
    }

    public void onSongAvailable(int songID) {

        Log.v(TAG, "intent received, playpath available for song " + getSongForID(songID));

        Song song = getSongForID(songID);

        if (getState() == STATE_WAITING && song == getCurrentSong()) {
            Log.v(TAG, "state is waiting and song is current song...");
            playCurrentSong();

        }
        // getCurrentSong().getMediaWrapper().play();
    }

    public void onSongNotAvailable(int intExtra) {

        Log.v(TAG, "intent received, try setting next wrapper for song with id " + intExtra);

        Song song = getSongForID(intExtra);
        if (trySettingNextWrapper(song)) {
            initializeSong(song);

        }


    }

    //test for git
}
