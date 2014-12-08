package com.example.mymodule.mediawrappers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.mymodule.mymodule.app.Song;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by charlotte on 06.12.14.
 */
public class PlayQueue {

    //TODO: muss in FileStreamingMediaService
    public static String SONG_AVAILABLE = "com.example.song_available";
    public static String SONG_NOT_AVAILABLE = "com.example.song_not_available";
    //TODO: das ist nur vorläufig:
    private ArrayList<String> mediaWrappersOrdered;
    private AbstractMediaWrapper mediaWrapper;
    private Song currentSong;
    private Context context;
    private int counter;
    private ArrayList<Song> songs;


    /**
     * This class should be used to create a playlist/queue with a list of songs.
     * The mediawrapper type that should be used has to be specified for every song (see Song class).
     *
     * @param context should be the parent activity, used for intents
     * @param songs   list of songs
     */
    public PlayQueue(Context context, ArrayList<Song> songs) {

        //TODO: this should be done somewhere else!


        this.context = context;
        this.songs = songs;

        BroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileStreamingMediaService.TRACK_FINISHED);
        intentFilter.addAction(PlayQueue.SONG_AVAILABLE);
        intentFilter.addAction(PlayQueue.SONG_NOT_AVAILABLE);
        context.registerReceiver(broadcastReceiver, intentFilter);


    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    /**
     * This method should be used by other classes to play the songs previously added
     * to the song list of the play queue.
     *
     */
    public void playSongs() {

        if (counter < songs.size() && counter >= 0) {

            setCurrentSong(songs.get(counter));
            String mediaWrapperType = currentSong.getMediaWrapperType();


            ArrayList<Song> songsTemp = new ArrayList<Song>();
            songsTemp.add(getCurrentSong());


            if (getCurrentSong().getMediaWrapper() == null) {


                AbstractMediaWrapper abstractMediaWrapper = null;

                //TODO: in Methode
                if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_LOCAL_FILE)) {
                    abstractMediaWrapper = new LocalFileStreamingMediaWrapper(context, songsTemp);

                } else if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD)) {
                    abstractMediaWrapper = new SoundCloudStreamingMediaWrapper(context, songsTemp);

                } else if (mediaWrapperType.equals(Song.MEDIA_WRAPPER_SPOTIFY)) {

                    abstractMediaWrapper = new SpotifyMediaWrapper(context, songsTemp);
                }


                getCurrentSong().setMediaWrapper(abstractMediaWrapper);

            }
            mediaWrapper = currentSong.getMediaWrapper();
            mediaWrapper.lookForSong();

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
        counter--;
        if (counter < 0)
            counter = songs.size() - 1;
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

        Log.d("", "is jumping to " + index);

        mediaWrapper.stopPlayer();

        counter = index;
        playSongs();


    }

    /**
     * Can be used by the GUI when pause button was pressed.
     */
    public void pausePlayer() {

        mediaWrapper.pausePlayer();
    }

    /**
     * Can be used by the GUI when resume button was pressed.
     */
    public void resumePlayer() {

        mediaWrapper.resumePlayer();
    }

    /**
     * Tries setting another wrapper if the current wrapper cannot play
     * the song. Not yet implemented.
     *
     * @return
     */
    private boolean trySettingNextWrapper() {


        return false;

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(FileStreamingMediaService.TRACK_FINISHED)) {

                Log.d("", "intent received, track finished");
                if (counter < songs.size())
                    nextTrack();

            } else if (intent.getAction().equals(PlayQueue.SONG_AVAILABLE)) {
                Log.d("", "intent received, playing next song with index: " + counter);
                getCurrentSong().getMediaWrapper().play();


            } else if (intent.getAction().equals(PlayQueue.SONG_NOT_AVAILABLE)) {

                Log.d("", "intent received, try setting next wrapper");
                trySettingNextWrapper();

            }
        }
    }


}
