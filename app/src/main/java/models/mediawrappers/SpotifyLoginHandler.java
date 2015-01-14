package models.mediawrappers;

/**
 * Created by charlotte on 28.12.14.
 */

import android.app.Activity;
import android.content.Context;


import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import de.lmu.playlist.domain.entity.SpotifyToken;
import models.apiwrappers.APIWrapper;
import models.apiwrappers.CallbackInterface;
import rest.client.ClientConstants;

/**
 * Created by charlotte on 20.12.14.
 *
 * @author charlotte
 */
public class SpotifyLoginHandler {


    public static final String TAG = "main.java.models.mediawrappers.SpotifyLoginHandler";

    public static final String CLIENT_ID = "605ac27c70444b499869422e93a492f8";
    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String REDIRECT_URI = "my-first-android-app-login://callback";
    public static final SpotifyLoginHandler instance = new SpotifyLoginHandler();
    public static final String CODE_STRING = "code";
    private static final String SPOTIFY_SHARED_PREF_STRING = "spotify-shared-preferences";
    private static final String SPOTIFY_REFRESH_TOKEN_STRING = "spotify-refresh-token";
    private static final String SPOTIFY_TOKENS_CALLBACK = "spotify-tokens-callback";
    private static final String SPOTIFY_ACCESS_TOKEN_CALLBACK = "spotify-access-token-callback";
    Activity context;
    private SharedPreferences preferences;
    private String currentAccessToken;

    public SpotifyLoginHandler() {

    }

    public SpotifyLoginHandler(Activity context) {
        this.context = context;

        preferences = context.getSharedPreferences(SPOTIFY_SHARED_PREF_STRING, 0);

    }

    public static SpotifyLoginHandler getInstance() {
        return instance;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public void openAuthWindow() {

        Log.d("", "open auth window");
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, RESPONSE_TYPE_CODE, REDIRECT_URI, new String[]{"user-read-private", "streaming"}, null, (Activity) context);

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

    public void getNewAccessToken(String refreshToken) {

        BasicNameValuePair authCodePair = new BasicNameValuePair("refresh_token", refreshToken);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(authCodePair);

        String url = ClientConstants.BASE_URL + "/spotify/refresh_token";
        url = APIWrapper.encodeURL(url, params);


        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        asyncHttpClient.get(getContext(), url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d(TAG, "back result is: " + s);
                //processWebCallResult("", DEFAULT_CALLBACK, null);

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "success! " + s);
                //  processWebCallResult(s, SPOTIFY_ACCESS_TOKEN_CALLBACK, null);
                //  Log.d(TAG, "back result is: "+s);
            }


        });


    }

    public boolean hasSpotifyRequestToken() {
        String refreshToken = retrieveRefreshToken();
        return (refreshToken != null && !(refreshToken.equals("")));

    }

    public void startSpotifyLogin() {
        //TODO: Abfrage nach Access Token?
        if (hasSpotifyRequestToken()) {
            getNewAccessToken(retrieveRefreshToken());

        } else {

            openAuthWindow();

        }

    }

    public void getAccessAndRefreshToken(String authCode) {


        BasicNameValuePair authCodePair = new BasicNameValuePair("auth_code", authCode);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(authCodePair);

        String url = ClientConstants.BASE_URL + "/spotify/get_tokens";
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


        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
       // Header[] headers = {new BasicHeader("Content-type", "application/json")};

        Log.d(TAG, url);

        asyncHttpClient.get(getContext(), url, null, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d(TAG, "back result is: " + s + throwable.getStackTrace() + " " + throwable.getMessage() + " " + throwable.getCause() + " " + throwable.toString());

                //  Gson gson = new Gson();
                //  SpotifyToken spotifyToken = gson.fromJson(s, SpotifyToken.class);

                //processWebCallResult("", DEFAULT_CALLBACK, null);

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "success! " + s);
                //  processWebCallResult(s, SPOTIFY_TOKENS_CALLBACK, null);
                //  Log.d(TAG, "back result is: "+s);
            }


        });


    }

    public String getCurrentAccessToken() {

        return currentAccessToken;

    }

    public void setCurrentAccessToken(String currentAccessToken) {
        this.currentAccessToken = currentAccessToken;
    }
}