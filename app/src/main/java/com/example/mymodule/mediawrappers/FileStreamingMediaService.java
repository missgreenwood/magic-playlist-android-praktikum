package com.example.mymodule.mediawrappers;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by lotta on 02.12.14.
 */
public class FileStreamingMediaService extends Service implements MediaPlayer.OnPreparedListener,
MediaPlayer.OnCompletionListener{

    MediaPlayer mediaPlayer = null;
    public static final String ACTION_PLAY = "com.example.action.PLAY";
    private String playPath;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("", "intent started");

        if (intent.getAction().equals(ACTION_PLAY)) {

            Log.d("", "start play");

            playPath = intent.getStringExtra("playpath");
            mediaPlayer = new MediaPlayer();  // initialize it here
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(playPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        }
        return START_STICKY; //TODO: sinnvoller return-Wert

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
       //TODO: send some stats as broadcast???
        this.stopSelf();

    }

}
