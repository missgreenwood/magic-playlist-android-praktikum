package models.mediawrappers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import controllers.MainActivity;
import tests.R;

/**
 * Created by charlotte on 15.01.15.
 */
public class SpotifyService extends Service {

    public static final String CLIENT_ID = "605ac27c70444b499869422e93a492f8";
    private SpotifyLoginHandler spotifyLoginHandler;
    private Spotify spotify;
    private Player mPlayer;
    private String playPath;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction().equals(SpotifyMediaWrapper.SET_TO_FOREGROUND)) {

            playPath = intent.getStringExtra(FileStreamingMediaService.INFO_PlAYPATH);
            String songname = intent.getStringExtra(FileStreamingMediaService.INFO_SONGNAME);
            String artist = intent.getStringExtra(FileStreamingMediaService.INFO_ARTIST);
            String mediaWrapperType = intent.getStringExtra(FileStreamingMediaService.INFO_MEDIA_WRAPPER);


            Notification notification = new Notification();
            notification.tickerText = "MagicPlaylist";
            notification.icon = R.drawable.metrodroid_music;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

            Intent toLaunch = new Intent(getApplicationContext(), MainActivity.class);
            toLaunch.setAction("android.intent.action.MAIN");
            toLaunch.addCategory("android.intent.category.LAUNCHER");

            PendingIntent intentBack = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

            String displayMediaWrapper = "";
            Resources resources = getApplicationContext().getResources();
            int resId = resources.getIdentifier(mediaWrapperType, "string", getPackageName());
            displayMediaWrapper = resources.getString(resId);
            String notificationText = artist + " - " + songname + " in " + displayMediaWrapper;


            notification.setLatestEventInfo(getApplicationContext(), "MagicPlaylist",
                    notificationText, intentBack);


            startForeground(6666, notification);

        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}