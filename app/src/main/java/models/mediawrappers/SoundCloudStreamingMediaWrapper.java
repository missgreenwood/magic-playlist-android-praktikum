package models.mediawrappers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;

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
import java.util.List;

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
    // private boolean playState = false;


    public SoundCloudStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public SoundCloudStreamingMediaWrapper(Context context, Song songs) {
        super(context, songs);
    }

    @Override
    public void processWebCallResult(String result, String callback, Bundle data) {


        BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);

        Log.v(TAG, "call process Web Call Result");

        int trackID = 0;


        try {

            if (result!=null) // result)
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
            //JSONObject first = (JSONObject) jsonArray.get(0);
            //  = first.getInt("id");

           // Log.v(TAG, "trackid: " + trackID);

            String newURL = "";
            if (trackID != 0) {
                newURL = SOUNDCLOUD_TRACKS_BASE_URL + trackID + "/" + SOUNDCLOUD_STREAM_STRING;
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(clientIDPair);
                newURL = APIWrapper.encodeURL(newURL, params);

                Log.v(TAG, "track_url :" + newURL);
                setPlayPath(newURL);

            }
            // play();


            // Intent intent = new Intent();

            if (newURL.equals("")) {
                // intent.setAction(PlayQueue.SONG_NOT_AVAILABLE);
                sendSongAvailableIntent(false);
            } else {
                // intent.setAction(PlayQueue.SONG_AVAILABLE);
                sendSongAvailableIntent(true);

            }

            //  intent.putExtra(PlayQueue.SONG_ID, getSongDb().getId());
            //  context.sendBroadcast(intent);

            // playState = false;
        } catch (JSONException e) {
           Log.e(TAG, "error while process webcall with callback: " + callback + " with message: " + e.getMessage());
        }
    }

    @Override
    public void computePlayPath(Song song) {
        // String playpath = null;


        //https://api.soundcloud.com/tracks/41772991/stream?client_id=9998e443138603b1b6be051350158448



        String url = SOUNDCLOUD_TRACKS_BASE_URL;
        BasicNameValuePair queryStringPair = new BasicNameValuePair(SOUNDCLOUD_QUERY_STRING, song.getArtist() + " " + song.getSongname());
        BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(clientIDPair);
        params.add(queryStringPair);
        final String url2 = APIWrapper.encodeURL(url, params);


       final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
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
                asyncHttpClient.get(getContext(), url2, headers, null, responseHandler);

            }







    });

    }

    @Override
    public boolean lookForSong() {

        //TODO: diese Methoden ist eigentlich nicht notwendig, weil sie nur computePlayPath aufruft
        computePlayPath(getSong());
        return true;

    }


}
