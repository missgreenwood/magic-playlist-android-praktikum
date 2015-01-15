package models.mediawrappers;

import android.content.Context;
import android.content.Intent;

import models.mediaModels.Song;

import org.json.JSONException;

/**
 * Created by lotta on 02.12.14.
 * @author charlotte
 *
 */
public abstract class AbstractMediaWrapper {

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    Context context;
    private Song song;
    private String playPath;
    private int counter;

    public abstract boolean play();

    public abstract boolean lookForSong();

    public abstract void stopPlayer();

    public abstract void pausePlayer();

    /* Called when play button is pressed: either resume player or start new song*/
    public abstract void resumePlayer();

    public abstract void computePlayPath(Song song) throws JSONException;


    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getPlayPath() {
        return playPath;
    }

    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    public void sendSongAvailableIntent(boolean available) {

        Intent intent = new Intent();
        intent.putExtra(PlayQueue.SONG_ID, getSong().getId());

        if (available) {
            intent.setAction(PlayQueue.SONG_AVAILABLE);
        } else {
            intent.setAction(PlayQueue.SONG_NOT_AVAILABLE);
        }

        context.sendBroadcast(intent);
    }
}
