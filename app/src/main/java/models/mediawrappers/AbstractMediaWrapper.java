package models.mediawrappers;

import android.content.Context;

import models.mediaModels.Song;

import org.json.JSONException;

import java.util.List;

/**
 * Created by lotta on 02.12.14.
 */
public abstract class AbstractMediaWrapper {

    Context context;
    private Song song;
    private String playPath;
    private int counter;

    public abstract boolean play();

    public abstract boolean lookForSong();

    //TODO: abstract method
    public abstract void stopPlayer();

    public abstract void pausePlayer();

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

}
