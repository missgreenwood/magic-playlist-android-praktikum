package models.metadatawrappers;

import android.util.Log;

import models.apiwrappers.APIWrapper;
import models.mediaModels.Song;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by charlotte on 19.12.14.
 */

//TODO: sollte das serverseitig passieren???
    //NEIN, erstmal nicht... damit sparen wir uns ressourcen ;) höchstens dann, wenn wir auf unsere eigenen playlists zugreifen anstatt/parallel zu lastfm zugreifen (andy)


public class LastfmMetadataWrapper extends AbstractMetadataWrapper {

    public static final String SIMILAR_ARTISTS_CALLBACK = "similar artists callback";
    public static final String TOP_TRACKS_CALLBACK = "top tracks callback";

    public static final String LASTFM_BASE_URL = "http://ws.audioscrobbler.com/2.0/";
    public static final String LASTFM_API_KEY = "b0bf622f9e06f86d9dd5b6ad28cd0fed";
    public static final String LASTFM_API_KEY_STRING = "api_key";
    public static final String LASTFM_FORMAT_STRING = "format";
    public static final String LASTFM_METHOD_STRING = "method";
    public static final String LASTFM_FORMAT_JSON = "json";
    public static final String LASTFM_LIMIT_STRING = "limit";

    public static final String LASTFM_GET_SIMILAR_METHOD = "artist.getSimilar";
    public static final String LASTFM_ARTIST_STRING = "artist";

    public static final String LASTFM_GET_TOP_TRACKS_METHOD = "artist.gettoptracks";

    private LastFmListener listener = null;

//    public static void main(String[] args) {
//        LastfmMetadataWrapper test = new LastfmMetadataWrapper(null);
//        test.findSimilarArtists("Radiohead", 5);
//    }

    public LastfmMetadataWrapper (LastFmListener listener) {
        this.listener = listener;
    }

    //TODO: sollte der Artist eine eigene Klasse haben? Artist-Namen sind nicht eindeutig...
    //ich denke erstmal nicht, notfalls schreiben wir das um, aber die beinhalten nicht sonderlich viel informationen... höchstens noch lastfm id?
    public String findSimilarArtists(String artist, int limit) {
        String url = LASTFM_BASE_URL;

        BasicNameValuePair keyPair = new BasicNameValuePair(LASTFM_API_KEY_STRING, LASTFM_API_KEY);
        BasicNameValuePair methodPair = new BasicNameValuePair(LASTFM_METHOD_STRING, LASTFM_GET_SIMILAR_METHOD);
        BasicNameValuePair artistPair = new BasicNameValuePair(LASTFM_ARTIST_STRING, artist);
        BasicNameValuePair limitPair = new BasicNameValuePair(LASTFM_LIMIT_STRING, "" + limit);
        BasicNameValuePair formatPair = new BasicNameValuePair(LASTFM_FORMAT_STRING, LASTFM_FORMAT_JSON);


        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(keyPair);
        params.add(methodPair);
        params.add(artistPair);
        params.add(limitPair);
        params.add(formatPair);


        url = APIWrapper.encodeURL(url, params);

        //APIWrapper apiWrapper=new APIWrapper();
        //String jsonArrayString = apiWrapper.getJSONCall(url, APIWrapper.GET);

        Log.d("", "URL: " + url);

        //TODO: String in Variable
        APIWrapper asyncHTTP = new APIWrapper(this, SIMILAR_ARTISTS_CALLBACK, APIWrapper.GET_METHOD);
        asyncHTTP.execute(url);

        return url;
    }

    public void findTopTracks(String artist, int limit) {
        String url = LASTFM_BASE_URL;

        BasicNameValuePair keyPair = new BasicNameValuePair(LASTFM_API_KEY_STRING, LASTFM_API_KEY);
        BasicNameValuePair methodPair = new BasicNameValuePair(LASTFM_METHOD_STRING, LASTFM_GET_TOP_TRACKS_METHOD);
        BasicNameValuePair artistPair = new BasicNameValuePair(LASTFM_ARTIST_STRING, artist);
        BasicNameValuePair limitPair = new BasicNameValuePair(LASTFM_LIMIT_STRING, "" + limit);
        BasicNameValuePair formatPair = new BasicNameValuePair(LASTFM_FORMAT_STRING, LASTFM_FORMAT_JSON);


        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(keyPair);
        params.add(methodPair);
        params.add(artistPair);
        params.add(limitPair);
        params.add(formatPair);


        url = APIWrapper.encodeURL(url, params);

        //TODO: String in Variable
        APIWrapper asyncHTTP = new APIWrapper(this, TOP_TRACKS_CALLBACK, APIWrapper.POST_METHOD);
        asyncHTTP.execute(url);
    }

    @Override
    public void processWebCallResult(String result, String callback) {
        if (listener == null) {
            return;
        }
        switch(callback) {
            case SIMILAR_ARTISTS_CALLBACK:
                try {
                    JSONArray artists = new JSONObject(result).getJSONObject("similarartists").getJSONArray("artist");
                    String[] artistsArray = new String[artists.length()];
                    for (int i = 0; i < artists.length(); i++) {
                        artistsArray[i] = artists.getJSONObject(i).getString("name");
                    }
                    listener.onSimilarArtistsCallback(artistsArray);
                } catch (Exception e) {
                    Log.e("ERROR while converting artists JSONString to JSONObject", e.getMessage());
                }
                break;
            case TOP_TRACKS_CALLBACK:
                try {
                    String artist = new JSONObject(result).getJSONObject("toptracks").getJSONObject("@attr").getString("artist");
                    JSONArray tracks = new JSONObject(result).getJSONObject("toptracks").getJSONArray("track");
                    Song[] tracksArray = new Song[tracks.length()];
                    for (int i = 0; i < tracks.length(); i++) {
                        tracksArray[i] = new Song(tracks.getJSONObject(i).getString("name"), artist);
                    }
                    listener.onTopTracksCallback(tracksArray);
                } catch (Exception e) {
                    Log.e("ERROR while converting artists JSONString to JSONObject", e.getMessage());
                }
                break;
        }

        //TODO: je nach Callback die passende Methode aufrufen
        //JSON String verarbeiten
        //irgendwelche Methoden aufrufen usw....
    }


}
