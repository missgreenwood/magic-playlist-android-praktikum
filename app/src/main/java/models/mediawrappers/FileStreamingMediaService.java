package models.mediawrappers;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import tests.R;

import java.io.IOException;

/**
 * Created by lotta on 02.12.14.
 * @author charlotte
 *
 * Foreground service for playing songs from local and remote http streaming sources.
 */
public class FileStreamingMediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {


    public static final String TAG = "main.java.models.mediawrappers.FileStreamingMediaService";

    public static final String ACTION_PLAY = "com.example.action.PLAY";
    public static final String ACTION_STOP = "com.example.action.STOP";
    public static final String ACTION_PAUSE = "com.example.action.PAUSE";
    public static final String ACTION_RESUME = "com.example.action.RESUME";
    public static final String INFO_PlAYPATH = "com.example.info.playpath";
    public static final String INFO_SONGNAME = "com.example.info.songname";
    public static final int NOTIFICATION_ID = 555;
    public static final String TRACK_FINISHED = "track finished";
    AudioState state;
    private String playPath;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "intent started");

        if (intent.getAction().equals(ACTION_PLAY)) {


            playPath = intent.getStringExtra(INFO_PlAYPATH);
            String songname = intent.getStringExtra(INFO_SONGNAME);

            //TODO: should do something


            //   PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
            //          new Intent(getApplicationContext(), TestActivity.class),
            //          PendingIntent.FLAG_UPDATE_CURRENT);
            //TODO: durch andere Activity ersetzen

            Notification notification = new Notification();
            notification.tickerText = "Musik";
            notification.icon = R.drawable.ic_launcher;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.setLatestEventInfo(getApplicationContext(), "MagicPlaylist",
                    "Playing " + songname, null);

            startForeground(NOTIFICATION_ID, notification);
            Log.d(TAG, "start play");

            mediaPlayer = new MediaPlayer();  // initialize it here
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(playPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        } else if (intent.getAction().equals(ACTION_PAUSE)) {

            Log.d(TAG, "has received intent with action pause, state is " + state + " is playing: " + isPlaying);
//            Log.d(TAG, "media player is playing: "+mediaPlayer.isPlaying());

            if (state == AudioState.Playing && mediaPlayer.isPlaying()) {
                Log.d(TAG, "set state to paused");

                try {

                    mediaPlayer.pause();
                    state = AudioState.Paused;
                } catch (IllegalStateException e) {

                    e.printStackTrace();
                }
            }
        } else if (intent.getAction().equals(ACTION_RESUME)) {

            if (state == AudioState.Paused) {

                try {
                    mediaPlayer.start();
                    state = AudioState.Playing;
                } catch (IllegalStateException e) {

                    e.printStackTrace();
                }
            }
            // }


        }
        return START_NOT_STICKY; //TODO: sinnvoller return-Wert
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.v(TAG, "on prepared called, set state to playing");

        try {
            mediaPlayer.start();
            state = AudioState.Playing;
        } catch (IllegalStateException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            mediaPlayer.release();
            state = AudioState.Stopped;
            Log.v(TAG, "on completion");
        } catch (IllegalStateException e) {

            e.printStackTrace();
        }

        //Was tun, wenn die Exception auftritt?
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(TRACK_FINISHED);
        sendBroadcast(broadcastIntent);


        //Intent broadcastIntent = new Intent("")

        //this.stopSelf();

        this.stopSelf();
    }

    public void onDestroy() {

        Log.v(TAG, "on destroy?");


        //  if (mediaPlayer.isPlaying()) {
        //   mediaPlayer.stop();
        // }
        state = AudioState.Stopped;

        if (mediaPlayer != null)

            mediaPlayer.release();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        {

            Log.e(TAG, "Media Player Error");

            mediaPlayer.reset();

            //TODO: should I send a song completed or a song not available intent??
            // mediaPlayer = null;
        }

        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //  Log.d(TAG, "onBufferingUpdate percent:" + percent);

        /*I had to implement this listener because otherwise Android complained...
        but we don't need it now*/
    }


    enum AudioState {
        Preparing,
        Paused,
        Playing,
        Stopped

    }


}
