package models.mediaModels;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import models.mediawrappers.AbstractMediaWrapper;
import models.mediawrappers.LocalFileStreamingMediaWrapper;
import models.mediawrappers.SoundCloudStreamingMediaWrapper;
import models.mediawrappers.SpotifyMediaWrapper;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by charlotte on 06.12.14.
 * @author charlotte, andy
 *
 * Represents the play queue comprised of a collection of songs that can be played one after another,
 * randomly or chosen individually.
 */
public class PlayQueue {


    public static final String TAG = "main.java.models.mediaModels.PlayQueue";

    //TODO: muss in FileStreamingMediaService
    public static final String SONG_AVAILABLE = "com.example.song_available";
    public static final String SONG_NOT_AVAILABLE = "com.example.song_not_available";
    public static final int STATE_WAITING = 0;
    public static final int STATE_ALREADY_PlAYING = 1;
    public static final int STATE_IDLE = 2;
    private int state = STATE_IDLE;
    private final static Object lockObject = new Object();
    private static final int STATE_PAUSED = 3;
    private static final PlayQueue instance = new PlayQueue();
    public static String SONG_ID = "com.example.song_id";
    private static boolean forwardMode = true;
    //TODO: das ist nur vorläufig:
    private Song currentSong;
    private Context context;
    private int counter;
    private ArrayList<Song> songs;
    //TODO: should be read from a preferences file or something
    private ArrayList<String> mediaWrappers;
    private Playlist currentPlaylist;
    private ArrayList<Listener> observers;
    private boolean autoPilotMode = false;
    private AbstractMediaWrapper currentSongMediaWrapper;
    private ArrayList<Song> initLocalSongs;
    private boolean initializing = false;

    /**
     * This class should be used to create a playlist/queue with a list of songs.
     * The mediawrapper type that should be used has to be specified for every song (see Song class).
     */
    private PlayQueue() {
        observers = new ArrayList<>();
        setState(STATE_IDLE);
    }

    public static PlayQueue getInstance() {
        return instance;
    }

    public boolean isAutoPilotMode() {
        return autoPilotMode;
    }

    public void setAutoPilotMode(boolean autoPilotMode) {
        this.autoPilotMode = autoPilotMode;
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void importPlaylist(Playlist playlist) {
        Log.d(TAG, "import playlist called!");

        currentPlaylist = playlist;
        songs = playlist.getSongsList();
        if (songs.size() > 0) {
            initializePlaylist(true);
            stopCurrentSong();
            counter = 0;
            setCurrentSong(songs.get(counter));
        } else {
            Log.e(TAG, "tried to import playlist without songs");
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        Log.d(TAG, "set state " + getState() + " to " + state);

        this.state = state;
    }

    public void unsetAllWrappers() {
        for (Song song : songs) {

            song.setMediaWrapper(null);
            song.setNotPlayable(false);
        }

    }


    public Song getSongForID(int songID) {

        Song currentSong = getCurrentSong();
        if (currentSong != null && currentSong.getId() == songID) {
            return currentSong;
        }

        if (songs != null) {
            for (Song song : songs) {

                if (song.getId() == songID)
                    return song;
            }
        }

        return null;
    }

    //TODO: should rather be in class Song?
    public String getNextType(Song song) {


        Log.v(TAG, "method getNextType: get next type for song: " + song.toString());

        ListIterator<String> it = mediaWrappers.listIterator();

        while (it.hasNext()) {

            String mediaWrapperType = it.next();
            Log.v(TAG, "mediaWrapperType in list: " + mediaWrapperType);
            if (mediaWrapperType.equals(song.getMediaWrapperType()) && it.hasNext())
                return it.next();
        }

        return null;

    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {

        Log.v(TAG, "set current song: " + currentSong);

        this.currentSong = currentSong;
        this.currentSongMediaWrapper = currentSong.getMediaWrapper();
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

        if (songs == null) {
            Log.d(TAG, "no songs list given");
            if (getCurrentSong() == null) {
                Log.d(TAG, "also no single song given, canceling playing song!");
                return; //neither single song nor playlist is given...
            }
        } else if (getCurrentSong() == null && counter < songs.size() && counter >= 0) {
            setCurrentSong(songs.get(counter));
        }


        playSongIntern();
    }

    private void playSongIntern() {
        if (currentSong != null) {
            Log.d(TAG, "playing song: " + currentSong.toString());
            AbstractMediaWrapper wrapper = currentSong.getMediaWrapper();
            if (wrapper != null) {
                String playpath = wrapper.getPlayPath();
                Log.d(TAG, "playpath: " + playpath);
                if ((playpath != null) && (!playpath.equals(""))) {
                    Log.d(TAG, "now we can play the current song: " + currentSong);
                    setState(STATE_ALREADY_PlAYING);
                    wrapper.play();
                    notifyNewSongPlaying(currentSong);
                } else setState(STATE_WAITING);
            } else setState(STATE_WAITING);
        }
    }

    private void initializeSong(final Song song) {
      //  Log.d(TAG, "song "+song+" is initialized... mediawrappers are: "+TextUtils.join(", ", mediaWrappers));
        String mediaWrapperType = song.getMediaWrapperType();
        if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_NOT_SET)) {
            setDefaultMediaWrapperTypeInQueue(song);
        }

        setMediaWrapperForType(song);

        if (song.getMediaWrapper() != null) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
                    Log.d(TAG, "no look for song: " + song);
                    song.getMediaWrapper().lookForSong();
//                }
//            }).start();
        }
    }


    public void setMediaWrapperForType(Song song) {


        String mediaWrapperType = song.getMediaWrapperType();
        AbstractMediaWrapper abstractMediaWrapper=null;

        switch (mediaWrapperType) {
            case Song.MEDIA_WRAPPER_LOCAL_FILE:
                abstractMediaWrapper = new LocalFileStreamingMediaWrapper(context, song);
                break;
            case Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD:
                abstractMediaWrapper = new SoundCloudStreamingMediaWrapper(context, song);
                break;
            case Song.MEDIA_WRAPPER_SPOTIFY:
                abstractMediaWrapper = new SpotifyMediaWrapper(context, song);
                break;
            case (Song.MEDIA_WRAPPER_NONE):
                abstractMediaWrapper = null;
                song.setNotPlayable(true);
                break;
        }
        song.setMediaWrapper(abstractMediaWrapper);
    }

    public void setDefaultMediaWrapperTypeInQueue(Song song)
    {
        song.setMediaWrapperType(mediaWrappers.get(0));
    }

    /**
     * @param overwrite Will overwrite existing media wrappers if true
     */
    public void initializePlaylist(final boolean overwrite) {
        if(songs!=null) {
            initLocalSongs = new ArrayList<>();
            initializing = true;
            for (Song song : songs) {
                Log.v(TAG, "initialize song: " + song.toString());

                // if (overwrite) {
                    song.setMediaWrapperType(Song.MEDIA_WRAPPER_NOT_SET);
                    initializeSong(song);
                // }
            }
            initializing = false;

        }
    }

    /**
     * Can be used by the GUI to go to the next track (forward button).
     *
     */
    public synchronized void nextTrack() {

        Log.v(TAG, "next track called");

        if (songs == null) {
            return;
        }

        do {
            counter++;
            if (counter >= songs.size()) {
                counter = 0;
            }
        }
        while (songs.get(counter).isNotPlayable());

        jumpToTrack(counter);

    }


    /**
     * Can be used by the GUI to go to the next track (back button).
     *
     */
    public synchronized void previousTrack() {

        Log.v(TAG, "previous track called");

        if (songs == null) {
            return;
        }

        do {
            counter--;
            if (counter < 0)
                counter = songs.size() - 1;
        }
        while (songs.get(counter).isNotPlayable());



        jumpToTrack(counter);

    }


    /**
     * Can be used by the GUI to play a random track.
     *
     */
    public void randomTrack() {
        if (songs == null) {
            return;
        }
        Random rand = new Random();
        counter = rand.nextInt(songs.size() - 1);
        jumpToTrack(counter);

    }


    /**
     * Can be used by the GUI to jump to a specific track.
     *
     * @param index track index
     */
    public synchronized void jumpToTrack(int index) {


        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
        String methodName = e.getMethodName();


        Log.v(TAG, "is jumping to " + index + " method was called by: " + methodName);

        stopCurrentSong();

        if (songs == null) {
            Log.e("ERROR", "no songs list given...");
            return;
        }
        counter = index;
        if (counter >= songs.size()) {
            counter = counter % songs.size();
        }
        if (counter >= 0) {
            setCurrentSong(songs.get(counter));
        }


        setState(STATE_IDLE);
        //playSongs();
        playCurrentSong();
    }

    /**
     * Can be used by the GUI when pause button was pressed.
     */
    public void pausePlayer() {
        Song currentSong = getCurrentSong();

        if (currentSong != null) {
            AbstractMediaWrapper wrapper = currentSong.getMediaWrapper();
            if (wrapper != null) {
                wrapper.pausePlayer();
                setState(STATE_PAUSED);
            }
        }
    }

    /**
     * Can be used by the GUI when resume button was pressed.
     */
    public void resumePlayer() {
        if (getState() == STATE_PAUSED) {
            getCurrentSong().getMediaWrapper().resumePlayer();
        } else if (getState() == STATE_IDLE) {
            playCurrentSong();
        }
    }

    /**
     * Tries setting another wrapper if the current wrapper cannot play
     * the song. Not yet implemented.
     *
     * @return song
     */
    private boolean trySettingNextWrapper(Song song) {


        Log.v(TAG, "try setting next wrapper for song: " + song.toString());

        String type = getNextType(song);


        if (type == null)
           type=Song.MEDIA_WRAPPER_NONE;


       // song.setMediaWrapper(null);
        song.setMediaWrapperType(type);

        Log.v(TAG, "new mediawrapper type: " + type);

        return true;

    }


    public void onTrackFinished() {
        Log.d(TAG, "on track finished called");

        if (songs != null && counter < songs.size())
            nextTrack();
    }

    public void onSongAvailable(int songID) {
        Log.d(TAG, "intent received, playpath available for song " + getSongForID(songID));
        Song song = getSongForID(songID);
        song.setNotPlayable(false);
        if (song != null) {
            Log.v(TAG, "state at onSongAvailable: " + getState() + " currentSong: " + getCurrentSong() + " song: " + song);
            if (getState() == STATE_WAITING && song == getCurrentSong()) {
                Log.d(TAG, "state is waiting and song is current song...");
                playCurrentSong();
            }
        }
        // getCurrentSong().getMediaWrapper().play();
    }

    public void onSongNotAvailable(int songId) {

        Log.v(TAG, "intent received, try setting next wrapper for song with id " + songId);

        Song song = getSongForID(songId);

        if (song != null) {
            if (trySettingNextWrapper(song)) {
                initializeSong(song);
            } else {
                Log.e(TAG, "could not find wrapper for song: " + song);
                if (song == currentSong) {
                    //TODO: should this be here?
                    nextTrack();
                }
                song.setNotPlayable(true);
                notifyCannotInitializeSong(song);
            }

        }
    }

    public synchronized void stopCurrentSong()
    {
        // Song currentSong = getCurrentSong();
        //  if (currentSong != null) {
        //    AbstractMediaWrapper wrapper = currentSong.getMediaWrapper();
        //   if (wrapper != null) {
        //       wrapper.stopPlayer();
        //  }
        // }

        if (currentSongMediaWrapper != null) {

            Log.d(TAG, "no it should STOP current song: " + currentSongMediaWrapper);
            currentSongMediaWrapper.stopPlayer();

        } else {

            Log.d(TAG, "it should stop current song here, but currentSongMediaWrapper seems to be null...");
        }
    }

    public void playSingleSong(Song song) {
        stopCurrentSong();
        initializeSong(song);
        setCurrentSong(song);
        playSongIntern();
    }

    public void setMediaWrappers(ArrayList<String> mediaWrappers) {
        Log.d(TAG, "set media wrappers to: " + TextUtils.join(", ", mediaWrappers));
        this.mediaWrappers = mediaWrappers;
        Log.d(TAG, "media wrappers were set to: " + TextUtils.join(", ", this.mediaWrappers));
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void addObserver(Listener observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Listener observer) {
        this.observers.remove(observer);
    }

    public void notifyNewSongPlaying(Song song)
    {
        for (Listener observer : observers) {
            observer.onNewSongPlaying(song);
        }
    }

    private void notifyCannotInitializeSong(Song song) {
        for (Listener observer : observers) {
            observer.onCannotInitializeSong(song);
        }
    }

    public AbstractMediaWrapper getCurrentSongMediaWrapper() {
        return currentSongMediaWrapper;
    }

    public void setCurrentSongMediaWrapper(AbstractMediaWrapper currentSongMediaWrapper) {
        this.currentSongMediaWrapper = currentSongMediaWrapper;
    }

    public void cancelCurrentSong() {

        if (currentSong != null) {
            currentSong.setNotPlayable(true);
            notifyCannotInitializeSong(getCurrentSong());

        }
    }

    public interface Listener {
        void onNewSongPlaying(Song song);
        void onCannotInitializeSong(Song song);
    }

    //test for git
}