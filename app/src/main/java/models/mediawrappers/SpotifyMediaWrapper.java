package models.mediawrappers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import models.apiwrappers.APIWrapper;
import models.mediaModels.PlayQueue;
import models.mediaModels.Song;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lotta on 02.12.14.
 * @author charlotte
 * MediaWrapper for Spotify; uses Spotify SDK
 */
public class SpotifyMediaWrapper extends RemoteFileStreamingMediaWrapper implements PlayerNotificationCallback, ConnectionStateCallback {


    public static final String TAG = "main.java.models.mediawrappers.SpotifyMediaWrapper";
    public static final String CLIENT_ID = "605ac27c70444b499869422e93a492f8";
    public static final String SET_TO_FOREGROUND = "set_to_foreground";
    public static String SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search";
    public static String TYPE_TRACK_STRING = "type";
    // public int counter;
    public static String TYPE_TRACK = "track";
    public static String SPOTIFY_QUERY_STRING = "q";
    private static boolean firstTime = false;
    private static Player mPlayer;
    // private List<Song> songs;
    // private String playPath;
    // private  Context context;
    private Spotify spotify;
    private SharedPreferences preferences;
    private SpotifyLoginHandler spotifyLoginHandler;


    public SpotifyMediaWrapper(Context context, Song songsTemp) {
        super(context, songsTemp);
        //  openAuthWindow();
        //  preferences = context.getSharedPreferences(SPOTIFY_SHARED_PREF_STRING, 0);
        // this.context=context;
        //  setSong(songsTemp);
        spotifyLoginHandler = SpotifyLoginHandler.getInstance();
        //  this.spotifyLoginHandler.setContext(getContext());

    }

    @Override
    public boolean play() {


        /*
        if (spotifyLoginHandler.getCurrentAccessToken()==null || spotifyLoginHandler.getCurrentAccessToken().equals(""))

        {
            spotifyLoginHandler.setContext(getContext());
            spotifyLoginHandler.startSpotifyLogin();

        }*/


        Log.d(TAG, "play song: " + getPlayPath());


        this.spotify = new Spotify();
        Config spotifyConfig = new Config(context, SpotifyLoginHandler.getInstance().getCurrentAccessToken(), CLIENT_ID);


        if (mPlayer != null && mPlayer.isInitialized()) {

            Log.d(TAG, "play song " + getPlayPath());
            mPlayer.play(getPlayPath());


        } else {


            mPlayer = spotify.getPlayer(spotifyConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {

                    Log.d(TAG, "player is initialised");
                    mPlayer.addConnectionStateCallback(SpotifyMediaWrapper.this);
                    mPlayer.addPlayerNotificationCallback(SpotifyMediaWrapper.this);
                    mPlayer.play(getPlayPath());
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });


            Intent foregroundIntent = new Intent(context, SpotifyService.class);
            foregroundIntent.setAction(SET_TO_FOREGROUND);
            foregroundIntent.putExtra(FileStreamingMediaService.INFO_PlAYPATH, getPlayPath());
            foregroundIntent.putExtra(FileStreamingMediaService.INFO_SONGNAME, getSong().getSongname());
            foregroundIntent.putExtra(FileStreamingMediaService.INFO_ARTIST, getSong().getArtist());
            foregroundIntent.putExtra(FileStreamingMediaService.INFO_MEDIA_WRAPPER, getSong().getMediaWrapperType());
            //  context.startService(foregroundIntent);

        }


        return false;
    }




    public void computePlayPath(Song song) {




        String url = SPOTIFY_SEARCH_URL;
        String songQueryString = "";
        songQueryString += "title:" + song.getSongname() + " artist:" + song.getArtist();

        BasicNameValuePair queryStringPair = new BasicNameValuePair(SPOTIFY_QUERY_STRING, songQueryString);
        BasicNameValuePair trackTypePair = new BasicNameValuePair(TYPE_TRACK_STRING, TYPE_TRACK);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(queryStringPair);
        params.add(trackTypePair);
       final String url2 = APIWrapper.encodeURL(url, params);
        //APIWrapper asyncHTTP = new APIWrapper(this, DEFAULT_CALLBACK, APIWrapper.GET_METHOD);
       // asyncHTTP.execute(url);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        final Header[] headers = {new BasicHeader("Content-type", "application/json")};

        Handler handler = new Handler(getContext().getMainLooper());
        handler.post(new Runnable() {


            @Override
            public void run() {

                TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {


                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Log.e(TAG, "onFailture ");
                        processWebCallResult(null, DEFAULT_CALLBACK, null);

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Log.v(TAG, "success! " + s);
                        processWebCallResult(s, DEFAULT_CALLBACK, null);
                    }

                };


                // responseHandler.setUseSynchronousMode(false);


                asyncHttpClient.get(getContext(),url2,headers,   null,responseHandler);

            }




        });

    }

    public boolean lookForSong() {


        //TODO: Methode ernsthaft!
        computePlayPath(getSong());

        return true;
    }


    @Override
    public void stopPlayer() {
if (mPlayer!=null) {
    mPlayer.pause();

}
    }

    @Override
    public void pausePlayer() {

        Log.d(TAG, "calling pause player...");
        if (mPlayer != null)

        {

            mPlayer.removePlayerNotificationCallback(SpotifyMediaWrapper.this);
            Log.d(TAG, "mplayer is being paused");
            mPlayer.pause();
            mPlayer.addPlayerNotificationCallback(SpotifyMediaWrapper.this);

        }
    }

    @Override
    public void resumePlayer() {

        mPlayer.removePlayerNotificationCallback(SpotifyMediaWrapper.this);

        if (mPlayer != null)
            mPlayer.resume();

        mPlayer.addPlayerNotificationCallback(SpotifyMediaWrapper.this);

    }


    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MainActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d("MainActivity", "Received connection message: " + s);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {


        if (eventType == EventType.END_OF_CONTEXT) {

            mPlayer.removePlayerNotificationCallback(SpotifyMediaWrapper.this);
            Log.d(TAG, "Playback event received: " + eventType.name());
            PlayQueue.getInstance().onTrackFinished();
            mPlayer.addPlayerNotificationCallback(SpotifyMediaWrapper.this);


        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    public void processWebCallResult(String result, String callback, Bundle data) {
        String uri = "";

        if (result!=null)

        {

            try {
                JSONObject spotifyJSONObject = new JSONObject(result);
                JSONObject trackListObject = spotifyJSONObject.getJSONObject("tracks");
                JSONArray trackListItems = trackListObject.getJSONArray("items");
                if (trackListItems.length() > 0) {
                    JSONObject first = trackListItems.getJSONObject(0);
                    uri = first.getString("uri");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if ((uri != null) && !(uri.equals(""))) {

            setPlayPath(uri);
            sendSongAvailableIntent(true);


        } else {

            sendSongAvailableIntent(false);

        }


    }

}