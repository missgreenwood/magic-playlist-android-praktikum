package controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import models.mediawrappers.FileStreamingMediaService;
import models.mediaModels.PlayQueue;


public class MyBroadcastReceiver extends BroadcastReceiver {

    //should be defined like that according to javadoc
    private static final String TAG = "main.java.controllers.MyBroadcastReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(FileStreamingMediaService.TRACK_FINISHED)) {
            if (context instanceof MainActivity) {
                PlayQueue.getInstance().onTrackFinished();
                Log.d(TAG, "intent received, track finished");
            }
        } else if (intent.getAction().equals(PlayQueue.SONG_AVAILABLE)) {
            Log.d(TAG, "received song available");
            if (context instanceof MainActivity) {
                //   PlayQueue.getInstance().onTrackFinished();
                PlayQueue.getInstance().onSongAvailable(intent.getIntExtra(PlayQueue.SONG_ID, -1));
            }

        } else if (intent.getAction().equals(PlayQueue.SONG_NOT_AVAILABLE)) {
            Log.d(TAG, "received song not available");
            if (context instanceof MainActivity) {
                PlayQueue.getInstance()
                        .onSongNotAvailable(intent.getIntExtra(PlayQueue.SONG_ID, -1));
            }
        }
    }
}

