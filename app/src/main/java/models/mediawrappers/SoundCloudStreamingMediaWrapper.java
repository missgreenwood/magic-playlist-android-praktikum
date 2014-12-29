package models.mediawrappers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import models.apiwrappers.APIWrapper;
import models.mediaModels.Song;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private boolean playState = false;


    public SoundCloudStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public SoundCloudStreamingMediaWrapper(Context context, List<Song> songs) {
        super(context, songs);
    }

    @Override
    public void processWebCallResult(String result, String callback) {


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


            // play();

            //TODO: send broadcast

            Intent intent = new Intent();

            if (newURL == null || newURL.equals("")) {
                intent.setAction(PlayQueue.SONG_NOT_AVAILABLE);
            } else {
                intent.setAction(PlayQueue.SONG_AVAILABLE);

            }

            context.sendBroadcast(intent);

            playState = false;
        } catch (JSONException e) {


        }


    }

    @Override
    public void computePlayPath(Song song) {
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

        //TODO: String in Variable
        APIWrapper asyncHTTP = new APIWrapper(this, DEFAULT_CALLBACK, APIWrapper.GET_METHOD);
        asyncHTTP.execute(url);


        //return playpath;
    }

    @Override
    public boolean lookForSong() {


        //TODO: diese Methoden ist eigentlich nicht notwendig, weil sie nur computePlayPath aufruft
        computePlayPath(getSong(counter));
        return true;

    }



}