package models.mediawrappers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import models.mymodule.app.Song;

import java.util.List;

/**
 * Created by lotta on 02.12.14.
 */

/**
 * For local AND remote files (http)!
 */
public abstract class FileStreamingMediaWrapper extends AbstractMediaWrapper {


    public int counter;

    public FileStreamingMediaWrapper(Context context, String playPath) {
        this.context = context;
        //TODO: sauberere Lösung?
        setPlayPath(playPath);

    }


    public FileStreamingMediaWrapper(Context context, List<Song> songs) {
        this.context = context;
        setSong(songs);


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
        //TODO: mit Exceptions

        Log.d("", "play song " + getSong(counter).getSongname());
        //this.setO
        Intent playIntent = new Intent(context, FileStreamingMediaService.class);
        playIntent.putExtra(FileStreamingMediaService.INFO_PlAYPATH, getPlayPath());
        playIntent.putExtra(FileStreamingMediaService.INFO_SONGNAME, getSong(counter).getSongname());
        playIntent.setAction(FileStreamingMediaService.ACTION_PLAY);
        context.startService(playIntent);
        return true;
    }


    public void stopPlayer() {
        Intent stopIntent = new Intent(context, FileStreamingMediaService.class);
        stopIntent.setAction(FileStreamingMediaService.ACTION_STOP);
        context.stopService(stopIntent);
    }


    @Override
    public void pausePlayer() {

        Log.d("", "pause player in file streaming media wrapper");
        Intent pauseIntent = new Intent(context, FileStreamingMediaService.class);
        pauseIntent.setAction(FileStreamingMediaService.ACTION_PAUSE);
        context.startService(pauseIntent);

    }


    @Override
    public void resumePlayer() {
        Log.d("", "resume player in file streaming media wrapper");
        Intent resumeIntent = new Intent(context, FileStreamingMediaService.class);
        resumeIntent.setAction(FileStreamingMediaService.ACTION_RESUME);
        context.startService(resumeIntent);


    }


}


