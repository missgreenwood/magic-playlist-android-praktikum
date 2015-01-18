package models.mediawrappers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import models.mediaModels.Song;


/**
 * @author charlotte
 * For local AND remote files (http)!
 */
public abstract class FileStreamingMediaWrapper extends AbstractMediaWrapper {


    public static final String TAG = "main.java.models.mediawrappers.FileStreamingMediaWrapper";

    public int counter;

    public FileStreamingMediaWrapper(Context context, String playPath) {
        setContext(context);
        setPlayPath(playPath);

    }


    public FileStreamingMediaWrapper(Context context, Song song) {
        this.context = context;
        setSong(song);


    }

    public abstract boolean lookForSong();

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public boolean play() {

        if (getPlayPath() == null || getPlayPath().equals(""))
            return false;

        Log.d(TAG, "play song " + getSong().getSongname());
        //this.setO
        Intent playIntent = new Intent(context, FileStreamingMediaService.class);
        playIntent.putExtra(FileStreamingMediaService.INFO_PlAYPATH, getPlayPath());
        playIntent.putExtra(FileStreamingMediaService.INFO_SONGNAME, getSong().getSongname());
        playIntent.putExtra(FileStreamingMediaService.INFO_ARTIST, getSong().getArtist());
        playIntent.putExtra(FileStreamingMediaService.INFO_MEDIA_WRAPPER, getSong().getMediaWrapperType());
        playIntent.setAction(FileStreamingMediaService.ACTION_PLAY);
        context.startService(playIntent);
        return true;
    }


    public void stopPlayer() {
        Log.d(TAG, "sent stop intent to media service...");
        Intent stopIntent = new Intent(context, FileStreamingMediaService.class);
        stopIntent.setAction(FileStreamingMediaService.ACTION_STOP);
        context.stopService(stopIntent);
    }


    @Override
    public void pausePlayer() {

        Log.v(TAG, "pause player in file streaming media wrapper");
        Intent pauseIntent = new Intent(context, FileStreamingMediaService.class);
        pauseIntent.setAction(FileStreamingMediaService.ACTION_PAUSE);
        context.startService(pauseIntent);

    }


    @Override
    public void resumePlayer() {
        Log.v(TAG, "resume player in file streaming media wrapper");
        Intent resumeIntent = new Intent(context, FileStreamingMediaService.class);
        resumeIntent.setAction(FileStreamingMediaService.ACTION_RESUME);
        context.startService(resumeIntent);


    }


}


