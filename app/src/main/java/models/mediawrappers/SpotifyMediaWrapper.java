package models.mediawrappers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import models.apiwrappers.APIWrapper;
import models.mediaModels.Song;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
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
    public static String SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search";
    public static String TYPE_TRACK_STRING = "type";
    // public int counter;
    public static String TYPE_TRACK = "track";
    public static String SPOTIFY_QUERY_STRING = "q";
    // private List<Song> songs;
    // private String playPath;
    // private  Context context;
    private Spotify spotify;
    private Player mPlayer;
    private SharedPreferences preferences;


    public SpotifyMediaWrapper(Context context, Song songsTemp) {
        super(context, songsTemp);
        //  openAuthWindow();
        //  preferences = context.getSharedPreferences(SPOTIFY_SHARED_PREF_STRING, 0);
        // this.context=context;
        //  setSong(songsTemp);

    }

    @Override
    public boolean play() {


        this.spotify = new Spotify();

        //TODO: das muss ich irgendwie auslagern!


        //TODO: nicht access token hardcoden!
        // String accessToken = "BQAikzApJ-5PxbXEEnou32JJeeCdNsY5BBGI2WnDt7C82jCEImuIR7XZgzR9SSiDRMsLnhodWU78sQkJ7AhMPOs5m-g3kgY3QCKUHdHouFjvG0DIa4zwmmkwXGFNDtXsXgotCfOefvFha9tb0xc4SONKC4Z0MoV-5hhN3F4";


        Config spotifyConfig = new Config(context, SpotifyLoginHandler.getInstance().getCurrentAccessToken(), CLIENT_ID);


        Log.d(TAG, "spotify play, config: " + (spotifyConfig == null));

        if (spotifyConfig != null) {
            Log.d(TAG, "config: " + spotifyConfig.cachePath + spotifyConfig.oauthToken);
            mPlayer = spotify.getPlayer(spotifyConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {

                    Log.d(TAG, "player is initialised");
                    mPlayer.addConnectionStateCallback(SpotifyMediaWrapper.this);
                    mPlayer.addPlayerNotificationCallback(SpotifyMediaWrapper.this);
                    mPlayer.play(getPlayPath());
                    // mPlayer.play("spotify:track:0LTZD4vTsp0EN1wXatc9IR");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });

        }
        return false;
    }


    public boolean lookForSong() {


        //TODO: Methode ernsthaft!
        computePlayPath(getSong());


        return true;
    }

    public void computePlayPath(Song song) {


        String url = SPOTIFY_SEARCH_URL;


        String songQueryString = "";
        songQueryString += "title:" + song.getSongname() + " artist:" + song.getArtist();

        BasicNameValuePair queryStringPair = new BasicNameValuePair(SPOTIFY_QUERY_STRING, songQueryString);
        BasicNameValuePair trackTypePair = new BasicNameValuePair(TYPE_TRACK_STRING, TYPE_TRACK);

        //  BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);


        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        // params.add(clientIDPair);
        params.add(queryStringPair);
        params.add(trackTypePair);


        url = APIWrapper.encodeURL(url, params);

        Log.d(TAG, "spotify url: " + url);

        //APIWrapper apiWrapper=new APIWrapper();
        //String jsonArrayString = apiWrapper.getJSONCall(url, APIWrapper.GET);

        APIWrapper asyncHTTP = new APIWrapper(this, DEFAULT_CALLBACK, APIWrapper.GET_METHOD);
        asyncHTTP.execute(url);


        /*
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncHTTP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            asyncHTTP.execute(url);


        */
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        asyncHttpClient.get(getContext(), url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e(TAG, "onFailture ");
                processWebCallResult("", DEFAULT_CALLBACK, null);

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "success! " + s);
                processWebCallResult(s, DEFAULT_CALLBACK, null);
            }


        });



        //  https://api.spotify.com/v1/search?q=title:paranoid+artist:radiohead&type=track


    }

    @Override
    public void stopPlayer() {

        // mPlayer.shutdown();
    }

    @Override
    public void pausePlayer() {

        if (mPlayer != null)
            mPlayer.pause();
    }

    @Override
    public void resumePlayer() {

        if (mPlayer != null)
            mPlayer.resume();
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
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    public void processWebCallResult(String result, String callback, Bundle data) {

        /*
        if (callback.equals(SPOTIFY_TOKENS_CALLBACK)) {


            //TODO: get access and refreseh tokens


        }


        else if (callback.equals(SPOTIFY_ACCESS_TOKEN_CALLBACK))
        {


            //TODO: get new access token for refresh token

            String refreshToken="";
            saveRefreshToken(refreshToken);
        }

        else {
        */

        String uri = "";
        try {
            JSONObject spotifyJSONObject = new JSONObject(result);
            JSONObject trackListObject = spotifyJSONObject.getJSONObject("tracks");
            JSONArray trackListItems = trackListObject.getJSONArray("items");
            JSONObject first = trackListItems.getJSONObject(0);

            Log.d(TAG, "trackListObject: " + trackListObject);

            uri = first.getString("uri");


            Log.d(TAG, "track uri: " + uri);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "result: " + result);
        // Intent intent = new Intent();


        if ((uri != null) && !(uri.equals(""))) {

            setPlayPath(uri);
            sendSongAvailableIntent(true);
            // intent.setAction(PlayQueue.SONG_AVAILABLE);
            // intent.putExtra(PlayQueue.SONG_ID, getSong().getSongID());

        } else {

            sendSongAvailableIntent(false);
            // intent.setAction(PlayQueue.SONG_NOT_AVAILABLE);

        }

        // getContext().sendBroadcast(intent);


        //    Intent intent=new Intent();
        //    Intent intent=new Intent();
        //  intent.setAction(PlayQueue.SONG_AVAILABLE);
//        context.sendBroadcast(intent);
        //  }
    }

    /*
    public void openAuthWindow() {

        Log.d("", "open auth window");
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, RESPONSE_TYPE_CODE, REDIRECT_URI, new String[]{"user-read-private", "streaming"}, null, (Activity)context);

    }


    public void saveRefreshToken(String refreshToken) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SPOTIFY_REFRESH_TOKEN_STRING, refreshToken);
        // Commit the edits!
        editor.commit();
    }


    public String retrieveRefreshToken() {

        return preferences.getString(SPOTIFY_REFRESH_TOKEN_STRING, null);
    }


    public void getNewAccessToken(String refreshToken)
    {

        BasicNameValuePair authCodePair = new BasicNameValuePair("refresh_token", refreshToken);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(authCodePair);

        String url="http://141.84.213.249:5050/playlist/spotify/refresh_token";
        url = APIWrapper.encodeURL(url, params);



        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        asyncHttpClient.get(getContext(), url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d(TAG, "back result is: "+s);
                //processWebCallResult("", DEFAULT_CALLBACK, null);

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "success! " + s);
                processWebCallResult(s, SPOTIFY_ACCESS_TOKEN_CALLBACK, null);
                //  Log.d(TAG, "back result is: "+s);
            }


        });




    }


    public boolean hasSpotifyRequestToken()
    {
        String refreshToken = retrieveRefreshToken();
        return (refreshToken!=null && !(refreshToken.equals("")));

    }


    public void startSpotifyLogin()
    {
        //TODO: Abfrage nach Access Token?
        if (hasSpotifyRequestToken())
        {
            getNewAccessToken(retrieveRefreshToken());

        }

        else {

            openAuthWindow();

        }

    }


   public void getAccessAndRefreshToken(String authCode)
   {




       BasicNameValuePair authCodePair = new BasicNameValuePair("auth_code", authCode);
       ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
       params.add(authCodePair);

        String url="http://141.84.213.249:5050/playlist/spotify/get_tokens";
      url = APIWrapper.encodeURL(url, params);

       Log.d(TAG, url);

       //APIWrapper apiWrapper=new APIWrapper();
       //String jsonArrayString = apiWrapper.getJSONCall(url, APIWrapper.GET);
        /*
        APIWrapper asyncHTTP = new APIWrapper(this, DEFAULT_CALLBACK, APIWrapper.GET_METHOD);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncHTTP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            asyncHTTP.execute(url);

        */
    //return playpath;

/*
       AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
       Header[] headers = {new BasicHeader("Content-type", "application/json")};
       asyncHttpClient.get(getContext(), url, headers, null, new TextHttpResponseHandler() {
           @Override
           public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
               Log.d(TAG, "back result is: "+s);
               //processWebCallResult("", DEFAULT_CALLBACK, null);

           }

           @Override
           public void onSuccess(int i, Header[] headers, String s) {
               Log.d(TAG, "success! " + s);
               processWebCallResult(s, SPOTIFY_TOKENS_CALLBACK, null);
             //  Log.d(TAG, "back result is: "+s);
           }


       });




   }*/
}