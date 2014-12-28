package models.mediawrappers;

/**
 * Created by charlotte on 28.12.14.
 */

import android.app.Activity;
import android.content.Context;


import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import android.net.Uri;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import models.apiwrappers.APIWrapper;
import models.apiwrappers.CallbackInterface;

/**
 * Created by charlotte on 20.12.14.
 */
public class SpotifyLoginHandler implements CallbackInterface {

    public static final String CLIENT_ID = "605ac27c70444b499869422e93a492f8";
    public static final String CLIENT_ID_STRING = "client_id";
    public static final String CLIENT_SECRET = "96a24ef105804182bab5f8e8f8e115be";
    public static final String CLIENT_SECRET_STRING = "client_secret";
    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String REDIRECT_URI = "my-first-android-app-login://callback";
    public static final String REDIRECT_URI_STRING = "redirect_uri";
    public static final String SPOTIFY_TOKENS_CALLBACK = "spotify_tokens_callback";
    public static final String TOKEN_BASE_URL = "https://accounts.spotify.com/api/token";
    public static final String GRANT_TYPE_STRING = "grant_type";
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    public static final String CODE_STRING = "code";

    Activity context;


    public SpotifyLoginHandler(Activity context) {
        this.context = context;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public void openAuthWindow() {
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, RESPONSE_TYPE_CODE, REDIRECT_URI, new String[]{"user-read-private", "streaming"}, null, context);

    }


    /* after the intent... Uri uri = intent.getData();*/
    public String getAuthorizationCode(Uri uri) {
        AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
        return response.getCode();
    }

    //TODO: nicht als Parameter
    public void requestAccessAndRefreshTokens(String authorizationCode) {
        APIWrapper apiWrapper = new APIWrapper(this, SPOTIFY_TOKENS_CALLBACK, APIWrapper.POST_METHOD);
        String url = TOKEN_BASE_URL;


        BasicNameValuePair grantTypePair = new BasicNameValuePair(GRANT_TYPE_STRING, GRANT_TYPE_AUTHORIZATION_CODE);
        BasicNameValuePair trackTypePair = new BasicNameValuePair(CODE_STRING, authorizationCode);
        BasicNameValuePair redirectPair = new BasicNameValuePair(REDIRECT_URI_STRING, REDIRECT_URI);
        BasicNameValuePair clientIdPair = new BasicNameValuePair(CLIENT_ID_STRING, CLIENT_ID);
        BasicNameValuePair clientSecretPair = new BasicNameValuePair(CLIENT_SECRET_STRING, CLIENT_SECRET);

        //  BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);


        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(grantTypePair);
        params.add(trackTypePair);
        params.add(redirectPair);
        params.add(clientIdPair);
        params.add(clientSecretPair);

        url = APIWrapper.encodeURL(url, params);

        Log.d("", "spotify url: " + url);

        //APIWrapper apiWrapper=new APIWrapper();
        //String jsonArrayString = apiWrapper.getJSONCall(url, APIWrapper.GET);

        apiWrapper.execute(url);


        //  https://api.spotify.com/v1/search?q=title:paranoid+artist:radiohead&type=track


    }

    @Override
    public void processWebCallResult(String result, String callback) {


        if (callback.equals(SPOTIFY_TOKENS_CALLBACK)) {


        }


    }
}