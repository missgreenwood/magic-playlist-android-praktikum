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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;


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
    Context context;
    private String currentAccessToken;

    public SpotifyLoginHandler() {

    }

    /*
    public SpotifyLoginHandler(Activity context) {
        this.context = context;
    }*/

    public static SpotifyLoginHandler getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void openAuthWindow() {

        Log.d("", "open auth window");
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, RESPONSE_TYPE_CODE, REDIRECT_URI, new String[]{"user-read-private", "streaming"}, null, (Activity) context);

    }

    public void saveRefreshToken(String refreshToken) {

        SharedPreferences preferences = context.getSharedPreferences(SPOTIFY_SHARED_PREF_STRING, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SPOTIFY_REFRESH_TOKEN_STRING, refreshToken);
        // Commit the edits!
        editor.apply();
    }

    public String retrieveRefreshToken() {
        Log.d(TAG, "retrieve refresh token");
        String token = context.getSharedPreferences(SPOTIFY_SHARED_PREF_STRING, 0).getString(SPOTIFY_REFRESH_TOKEN_STRING, null);
        Log.d(TAG, "token: " + token);
        return token;
    }

    public void getNewAccessToken() {

        Log.d(TAG, "get new access token");

        String refreshToken = retrieveRefreshToken();

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

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "success! " + s);


                try {
                    JSONObject tokens = new JSONObject(s);
                    String accessToken = tokens.getString("access_token");
                    int expiresIn = tokens.getInt("expires_in");
                    setCurrentAccessToken(accessToken, expiresIn);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }


        });


    }

    public boolean hasSpotifyRequestToken() {
        String refreshToken = retrieveRefreshToken();
        return (refreshToken != null && !(refreshToken.equals("")));

    }

    public void startSpotifyLogin() {
        //TODO: Abfrage nach Access Token?

        if ((getCurrentAccessToken() != null) && !(currentAccessToken.equals(""))) {
            Log.d(TAG, "already has access token");

        } else if (hasSpotifyRequestToken()) {

            Log.d(TAG, "has refresh token, get new access token");
            getNewAccessToken();

        } else {

            Log.d(TAG, "no refresh token, open auth window");
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


        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
       // Header[] headers = {new BasicHeader("Content-type", "application/json")};

        Log.d(TAG, url);

        asyncHttpClient.get(getContext(), url, null, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.d(TAG, "back result is: " + s + throwable.getStackTrace() + " " + throwable.getMessage() + " " + throwable.getCause() + " " + throwable.toString());

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.d(TAG, "success! " + s);
                //  processWebCallResult(s, SPOTIFY_TOKENS_CALLBACK, null);
                //  Log.d(TAG, "back result is: "+s);

                try {
                    JSONObject tokens = new JSONObject(s);
                    String accessToken = tokens.getString("access_token");
                    String refreshToken = tokens.getString("refresh_token");
                    int expiresIn = tokens.getInt("expires_in");

                    Log.d(TAG, "access token: " + accessToken);

                    if (refreshToken != null && !(refreshToken.equals(""))) {

                        Log.v(TAG, "saved refresh_token " + refreshToken);
                        saveRefreshToken(refreshToken);

                    }
                    if (accessToken != null && !(accessToken.equals(""))) {

                        Log.v(TAG, "set new access token " + accessToken);

                        setCurrentAccessToken(accessToken, expiresIn);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });


    }

    private void scheduleGettingNewAccessToken(double v) {


        Log.d(TAG, "new access token in " + v + " seconds");
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                getNewAccessToken();
                return null;
            }
        }, (long) (v * 0.8), TimeUnit.SECONDS);

    }

    public String getCurrentAccessToken() {

        return currentAccessToken;

    }

    public void setCurrentAccessToken(String currentAccessToken, long expiryTime) {
        Log.d(TAG, "set current access token: " + currentAccessToken);
        this.currentAccessToken = currentAccessToken;
        scheduleGettingNewAccessToken(expiryTime);
    }

    public boolean isLoggedIn() {

        //TODO: additional check


        return ((getCurrentAccessToken() != null) && (!getCurrentAccessToken().equals("")));
    }
}