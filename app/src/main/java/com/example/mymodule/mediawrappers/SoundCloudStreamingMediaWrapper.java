package com.example.mymodule.mediawrappers;

import android.content.Context;
import android.util.Log;

import com.example.mymodule.apiwrappers.APIWrapper;
import com.example.mymodule.mymodule.app.Song;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by charlotte on 05.12.14.
 */
public class SoundCloudStreamingMediaWrapper extends RemoteFileStreamingMediaWrapper{

    public static final String SOUNDCLOUD_CLIENT_ID="9998e443138603b1b6be051350158448";
    public static final String SOUNDCLOUD_CLIENT_ID_STRING="client_id";
    public static final String SOUNDCLOUD_QUERY_STRING="q";
    public static final String GET_METHOD="GET";
    public static final String SOUNDCLOUD_TRACKS_BASE_URL="https://api.soundcloud.com/tracks/";
    public static final String SOUNDCLOUD_STREAM_STRING="stream";




    public SoundCloudStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public SoundCloudStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
    }

    @Override
    public void processWebCallResult(String result) {


        BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);

        Log.d("", "call process Web Call Result");
        try {
            JSONArray jsonArray = new JSONArray(result);

            int trackID=0;

            for (int i=0; i <jsonArray.length(); i++)
            {
                JSONObject row = jsonArray.getJSONObject(i);
                if (row.getBoolean("streamable"))
                {
                    trackID=row.getInt("id");
                    break;
                }

            }


            //JSONObject first = (JSONObject) jsonArray.get(0);
          //  = first.getInt("id");

            Log.d("", "trackid: "+trackID);

            String newURL=SOUNDCLOUD_TRACKS_BASE_URL+trackID+"/"+SOUNDCLOUD_STREAM_STRING;
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(clientIDPair);
            newURL=APIWrapper.encodeURL(newURL, params);

            Log.d("","track_url :"+newURL);
            setPlayPath(newURL);
            play();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void computePlayPath(Song song) throws JSONException {
        String playpath = null;


        //https://api.soundcloud.com/tracks/41772991/stream?client_id=9998e443138603b1b6be051350158448

        String url=SOUNDCLOUD_TRACKS_BASE_URL;


        BasicNameValuePair queryStringPair = new BasicNameValuePair(SOUNDCLOUD_QUERY_STRING,   song.getSongname());
        BasicNameValuePair clientIDPair = new BasicNameValuePair(SOUNDCLOUD_CLIENT_ID_STRING, SOUNDCLOUD_CLIENT_ID);


        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(clientIDPair);
        params.add(queryStringPair);


        url=APIWrapper.encodeURL(url,params);

        //APIWrapper apiWrapper=new APIWrapper();
        //String jsonArrayString = apiWrapper.getJSONCall(url, APIWrapper.GET);

        APIWrapper asyncHTTP = new APIWrapper(this);
        asyncHTTP.execute(url);


        //return playpath;
    }

    @Override
    public void playSong() {
        try {
            computePlayPath(getSong());

                    /* This will call play() method itself when its the right time!*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
