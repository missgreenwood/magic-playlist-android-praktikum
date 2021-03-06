package models.metadatawrappers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.TextHttpResponseHandler;

import models.apiwrappers.APIWrapper;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by charlotte on 19.12.14.
 *
 * @author TheDaAndy, charlotte
 */


public class LastfmMetadataWrapper extends AbstractMetadataWrapper {

    public static final String TAG = "main.java.models.metadatawrappers.LastfmMetadataWrapper";

    public static final String SIMILAR_ARTISTS_CALLBACK = "similar artists callback";
    public static final String TOP_TRACKS_CALLBACK = "top tracks callback";
    public static final String TAG_ARTISTS_CALLBACK = "tag artists callback";

    public static final String LASTFM_BASE_URL = "http://ws.audioscrobbler.com/2.0/";
    public static final String LASTFM_API_KEY = "b0bf622f9e06f86d9dd5b6ad28cd0fed";
    public static final String LASTFM_API_KEY_STRING = "api_key";
    public static final String LASTFM_FORMAT_STRING = "format";
    public static final String LASTFM_METHOD_STRING = "method";
    public static final String LASTFM_FORMAT_JSON = "json";
    public static final String LASTFM_LIMIT_STRING = "limit";

    public static final String LASTFM_GET_SIMILAR_METHOD = "artist.getSimilar";
    public static final String LASTFM_GET_TOP_TRACKS_METHOD = "artist.gettoptracks";
    public static final String LASTFM_GET_TAG_ARTISTS_METHOD = "tag.getTopArtists";
    public static final String LASTFM_SEARCH_ARTIST_METHOD = "artist.search";

    public static final String LASTFM_ARTIST_STRING = "artist";
    public static final String LASTFM_TAG_STRING = "tag";

    private LastFmListener listener = null;

    private Context context = null;

    public LastfmMetadataWrapper(LastFmListener listener) {
        this.listener = listener;
    }

    public void findSimilarArtists(String artist, int limit) {
        String url = LASTFM_BASE_URL;

        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(LASTFM_API_KEY_STRING, LASTFM_API_KEY));
        params.add(new BasicNameValuePair(LASTFM_METHOD_STRING, LASTFM_GET_SIMILAR_METHOD));
        params.add(new BasicNameValuePair(LASTFM_ARTIST_STRING, artist));
        params.add(new BasicNameValuePair(LASTFM_LIMIT_STRING, "" + limit));
        params.add(new BasicNameValuePair(LASTFM_FORMAT_STRING, LASTFM_FORMAT_JSON));


        url = APIWrapper.encodeURL(url, params);

        Bundle data = new Bundle();
        data.putString("artist", artist);

        makeCall(url, SIMILAR_ARTISTS_CALLBACK, data);
    }

    public void findTopTracks(String artist, int limit) {
        String url = LASTFM_BASE_URL;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(LASTFM_API_KEY_STRING, LASTFM_API_KEY));
        params.add(new BasicNameValuePair(LASTFM_METHOD_STRING, LASTFM_GET_TOP_TRACKS_METHOD));
        params.add(new BasicNameValuePair(LASTFM_ARTIST_STRING, artist));
        params.add(new BasicNameValuePair(LASTFM_LIMIT_STRING, "" + limit));
        params.add(new BasicNameValuePair(LASTFM_FORMAT_STRING, LASTFM_FORMAT_JSON));


        url = APIWrapper.encodeURL(url, params);

        final Bundle data = new Bundle();
        data.putString("artist", artist);

        makeCall(url, TOP_TRACKS_CALLBACK, data);
    }

    public void findGenreArtists(String genre, int limit) {
        String url = LASTFM_BASE_URL;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(LASTFM_API_KEY_STRING, LASTFM_API_KEY));
        params.add(new BasicNameValuePair(LASTFM_METHOD_STRING, LASTFM_GET_TAG_ARTISTS_METHOD));
        params.add(new BasicNameValuePair(LASTFM_TAG_STRING, genre));
        params.add(new BasicNameValuePair(LASTFM_LIMIT_STRING, "" + limit));
        params.add(new BasicNameValuePair(LASTFM_FORMAT_STRING, LASTFM_FORMAT_JSON));


        url = APIWrapper.encodeURL(url, params);
        makeCall(url, TAG_ARTISTS_CALLBACK, null);
    }

    public RequestHandle findGenreArtists(String artist, int limit, final LastFmListener.SearchArtistListener listener) {
        String url = LASTFM_BASE_URL;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(LASTFM_API_KEY_STRING, LASTFM_API_KEY));
        params.add(new BasicNameValuePair(LASTFM_METHOD_STRING, LASTFM_SEARCH_ARTIST_METHOD));
        params.add(new BasicNameValuePair(LASTFM_ARTIST_STRING, artist));
        params.add(new BasicNameValuePair(LASTFM_LIMIT_STRING, "" + limit));
        params.add(new BasicNameValuePair(LASTFM_FORMAT_STRING, LASTFM_FORMAT_JSON));


        url = APIWrapper.encodeURL(url, params);

        makeCall(url, TAG_ARTISTS_CALLBACK, null);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        return asyncHttpClient.get(context, url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (listener != null) {
                    listener.onSearchArtistError();
                }
            }
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (listener != null) {
                    String[] firstAttribs = {"results", "artistmatches", "artist"},
                            globalAttribs = null,
                            getAttribs = {"mbid", "name"};
                    String[][] artistsArray = convertJSONStringToArray(s, firstAttribs, getAttribs, globalAttribs);
                    listener.onSearchArtistSuccess(artistsArray);
                }
            }
        });
    }

    private void makeCall(String url, final String callback, final Bundle data) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        asyncHttpClient.get(context, url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                processWebCallResult("", callback, data);

            }
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                processWebCallResult(s, callback, data);
            }
        });
    }

    @Override
    public void processWebCallResult(String result, String callback, Bundle data) {
        if (listener == null) {
            return;
        }

        switch (callback) {
            case SIMILAR_ARTISTS_CALLBACK: {
                String[] firstAttribs = {"similarartists", "artist"},
                        globalAttribs = {"artist"},
                        getAttribs = {"mbid", "name", "match"};
                String[][] artistsArray = convertJSONStringToArray(result, firstAttribs, getAttribs, globalAttribs);
                listener.onSimilarArtistsCallback(data.getString("artist"), globalAttribs[0], artistsArray);
                break;
            }
            case TOP_TRACKS_CALLBACK: {
                String[] firstAttribs = {"toptracks", "track"},
                        globalAttribs = {"artist"},
                        getAttribs = {"name"};
                String[][] fetchedTacksArray = convertJSONStringToArray(result, firstAttribs, getAttribs, globalAttribs);
                ArrayList<String> songNames = new ArrayList<>();
                if (fetchedTacksArray != null) {
                    for (int i = 0; i < fetchedTacksArray.length; i++) {
                        songNames.add(fetchedTacksArray[i][0]);
                    }
                }
                listener.onTopTracksCallback(data.getString("artist"), globalAttribs[0], songNames);
                break;
            }
            case TAG_ARTISTS_CALLBACK: {
                String[] firstAttribs = {"topartists", "artist"},
                        globalAttribs = null,
                        getAttribs = {"mbid", "name"};
                String[][] artistsArray = convertJSONStringToArray(result, firstAttribs, getAttribs, globalAttribs);
                listener.onGenreArtistsCallback(artistsArray);
                break;
            }
        }
    }

    /**
     * converts a JSON string into its relevant values
     * to get the relevant infos you have to get through some first attribs like "similarartists->artist"
     * this object then has an array of all artists found.
     * This function returns an global attribute found in the "similarartists" object and for each element in the array the in arrayValueAttribs defined attributeValues.
     *
     * @param jsonString        the jsonString returned by lastfm call
     * @param firstAttribs      the attrib steps that have to be made to get to relevant infos (like: [similarartists, artist])
     * @param arrayValueAttribs the fetched attributeValues of each element in array
     * @param globalAttributes  (optional) the attributeKeys saved in first Step Object (in this case "similarartists").
     *                          For toptracks it would be ["artist"]. the values will replace the attribKeys directly in the given array.
     */
    private String[][] convertJSONStringToArray(String jsonString, String[] firstAttribs, String[] arrayValueAttribs, String[] globalAttributes) {
        String[][] arrayValues = null;
        try {
            JSONObject containerObject = new JSONObject(jsonString).getJSONObject(firstAttribs[0]);

            //get global attributes (e.g. artist)
            if (globalAttributes != null && globalAttributes.length > 0) {
                JSONObject globalAttributesContainer = containerObject.getJSONObject("@attr");
                for (int i = 0; i < globalAttributes.length; i++) {
                    globalAttributes[i] = globalAttributesContainer.getString(globalAttributes[i]);
                }
            }

            int i = 1;
            //get JSONArray with interesting elements (artist/track)
            if (firstAttribs.length > 2) { //searchArtist has special
                for (;i < firstAttribs.length - 1; i++) {
                    containerObject = containerObject.getJSONObject(firstAttribs[i]);
                }
            }

            String lastAttribKey = firstAttribs[firstAttribs.length-1];

            JSONArray elementsJsonArray;
            Object elementsArray = containerObject.get(lastAttribKey);
            if (elementsArray instanceof JSONArray) {
                elementsJsonArray = containerObject.getJSONArray(lastAttribKey);
            } else if (elementsArray instanceof JSONObject) {
                JSONObject element = containerObject.getJSONObject(lastAttribKey);
                elementsJsonArray = new JSONArray().put(element);
            } else {
                return new String[0][0];
            }

            //getSongDb normal String array from JSONObjects with interesting attributes
            arrayValues = new String[elementsJsonArray.length()][arrayValueAttribs.length];
            for (int elIndex = 0; elIndex < elementsJsonArray.length(); elIndex++) {
                JSONObject element = elementsJsonArray.getJSONObject(elIndex);
                for (int attrIndex = 0; attrIndex < arrayValueAttribs.length; attrIndex++) {
                    arrayValues[elIndex][attrIndex] = element.getString(arrayValueAttribs[attrIndex]);
                }
            }
        } catch (Exception e) {
            //invalid json is given, simply return null...
        }
        return arrayValues;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
