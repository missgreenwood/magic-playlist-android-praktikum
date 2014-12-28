package controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import models.mediawrappers.FileStreamingMediaService;
import models.mediawrappers.PlayQueue;


//TODO: Testactivity muss hier durch die KORREKTE Activity ersetzt werden!

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(FileStreamingMediaService.TRACK_FINISHED)) {

            if (context instanceof TestActivity) { //TODO: replace with actual Activity

                Log.d("", "hier kommen die broadcasts an...");
                TestActivity testActivity = (TestActivity) context;
                PlayQueue playQueue = testActivity.getPlayQueue();
                playQueue.onTrackFinished();


                Log.d("", "intent received, track finished");


            }
        } else if (intent.getAction().equals(PlayQueue.SONG_AVAILABLE)) {
            if (context instanceof TestActivity) { //TODO: replace with actual Activity


                TestActivity testActivity = (TestActivity) context;
                PlayQueue playQueue = testActivity.getPlayQueue();
                //   playQueue.onTrackFinished();

                playQueue.onSongAvailable();


            }

        } else if (intent.getAction().equals(PlayQueue.SONG_NOT_AVAILABLE)) {

            if (context instanceof TestActivity) { //TODO: replace with actual Activity
                TestActivity testActivity = (TestActivity) context;
                PlayQueue playQueue = testActivity.getPlayQueue();

                playQueue.onSongNotAvailable();


            }
        }
    }
}

