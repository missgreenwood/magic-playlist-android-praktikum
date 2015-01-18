package models.mediawrappers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import controllers.MainActivity;
import models.apiwrappers.APIWrapper;
import models.mediaModels.Song;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import com.loopj.android.http.*;

/**
 * Created by charlotte on 05.12.14.
 * @author charlotte
 * MediaWrapper for Soundcloud.
 */
public class SoundCloudStreamingMediaWrapper extends RemoteFileStreamingMediaWrapper {

    public static final String TAG = "main.java.mediawrappers.SoundCloudStreamingMediaWrapper";

    public static final String SOUNDCLOUD_CLIENT_ID = "9998e443138603b1b6be051350158448";
    public static final String SOUNDCLOUD_CLIENT_ID_STRING = "client_id";
    public static final String SOUNDCLOUD_QUERY_STRING = "q";
    // public static final String GET_METHOD = "GET";
    public static final String SOUNDCLOUD_TRACKS_BASE_URL = "https://api.soundcloud.com/tracks/";
    public static final String SOUNDCLOUD_STREAM_STRING = "stream";



/*
    public SoundCloudStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);

    }*/

    public SoundCloudStreamingMediaWrapper(Context context, Song songs) {
        super(context, songs);

    }


    @Override
    public void processWebCallResult(String result, String callback, Bundle data) {

        BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);
        Log.v(TAG, "call process Web Call Result");
        int trackID = 0;

        try {
            if (result != null)
            {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);
                    if (row.getBoolean("streamable")) {
                        trackID = row.getInt("id");
                        break;
                    }
                }
            }


            String newURL = "";
            if (trackID != 0) {
                newURL = SOUNDCLOUD_TRACKS_BASE_URL + trackID + "/" + SOUNDCLOUD_STREAM_STRING;
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(clientIDPair);
                newURL = APIWrapper.encodeURL(newURL, params);

                Log.v(TAG, "track_url :" + newURL);
                setPlayPath(newURL);
            }
            if (newURL.equals("")) {

                sendSongAvailableIntent(false);
            } else {

                sendSongAvailableIntent(true);
            }

        } catch (JSONException e) {
           Log.e(TAG, "error while process webcall with callback: " + callback + " with message: " + e.getMessage());
        }
    }

    @Override
    public void computePlayPath(Song song) {
        String url = SOUNDCLOUD_TRACKS_BASE_URL;
        BasicNameValuePair queryStringPair = new BasicNameValuePair(SOUNDCLOUD_QUERY_STRING, song.getArtist() + " " + song.getSongname());
        BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(clientIDPair);
        params.add(queryStringPair);
        final String url2 = APIWrapper.encodeURL(url, params);


       final  Header[] headers = {new BasicHeader("Content-type", "application/json")};

        Handler handler = new Handler(getContext().getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {

                TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        processWebCallResult(null, DEFAULT_CALLBACK, null);

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Log.d(TAG, "success! " + s);
                        processWebCallResult(s, DEFAULT_CALLBACK, null);
                    }

                };
                MainActivity.asyncHttpClient.get(getContext(), url2, headers, null, responseHandler);

            }
    });

    }

    @Override
    public boolean lookForSong() {
        computePlayPath(getSong());
        return true;

    }


}
