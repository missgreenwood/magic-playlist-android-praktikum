package com.example.mymodule.mediawrappers;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.mymodule.mymodule.app.Song;

import org.json.JSONException;

/**
 * Created by lotta on 02.12.14.
 */

/**
 * For local AND remote files (http)!
 */
public abstract class FileStreamingMediaWrapper extends AbstractMediaWrapper {


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public FileStreamingMediaWrapper(Context context, String playPath) {
        this.context = context;
        //TODO: sauberere Lösung?
        this.playPath = playPath;

    }

    public FileStreamingMediaWrapper(Context context, Song song) {
        this.context = context;
        this.song = song;
        try {
           computePlayPath(song);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    protected abstract void computePlayPath(Song song) throws JSONException;


    public String getPlayPath() {
        return playPath;
    }

    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }

    Context context;
    private Song song;
    private String playPath;

    public abstract void playSong();

    @Override
    public boolean play() {

        Log.d("", "play song");
        Intent playIntent = new Intent(context, FileStreamingMediaService.class);
        playIntent.putExtra("playpath", getPlayPath());
        playIntent.setAction(FileStreamingMediaService.ACTION_PLAY);
        context.startService(playIntent);
        return true;
    }
}
