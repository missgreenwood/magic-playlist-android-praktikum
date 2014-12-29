package models.metadatawrappers;

import android.util.Log;

import models.apiwrappers.APIWrapper;
import models.mediaModels.Song;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
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
            {
                String[] firstAttribs = {"similarartists", "artist"},
                        getAttribs = {"name"};
                String[][] artistsArray = convertJSONStringToArray(result, firstAttribs, getAttribs, null);
                String[] artistsNames = new String[artistsArray.length];
                for (int i = 0; i < artistsArray.length; i++) {
                    artistsNames[i] = artistsArray[i][0]; //value 0 is name
                }
                listener.onSimilarArtistsCallback(artistsNames);
                break;
            }
            case TOP_TRACKS_CALLBACK:
            {
                String[] firstAttribs = {"similarartists", "artist"},
                        globalAttribs = {"artist"},
                        getAttribs = {"name"};
                String[][] fetchedTacksArray = convertJSONStringToArray(result, firstAttribs, getAttribs, globalAttribs);
                Song[] songsArray = new Song[fetchedTacksArray.length];
                for (int i = 0; i < fetchedTacksArray.length; i++) {
                    songsArray[i] = new Song(globalAttribs[0], fetchedTacksArray[i][0]);
                }
                listener.onTopTracksCallback(songsArray);
                break;
            }
        }

        //TODO: je nach Callback die passende Methode aufrufen
        //JSON String verarbeiten
        //irgendwelche Methoden aufrufen usw....
    }

    /**
     * converts a JSON string into its relevant values
     * to get the relevant infos you have to get through some first attribs like "similarartists->artist"
     * this object then has an array of all artists found.
     * This function returns an global attribute found in the "similarartists" object and for each element in the array the in arrayValueAttribs defined attributeValues.
     * @param jsonString the jsonString returned by lastfm call
     * @param firstAttribs the attrib steps that have to be made to get to relevant infos (like: [similarartists, artist])
     * @param arrayValueAttribs the fetched attributeValues of each element in array
     * @param globalAttributes (optional) the attributeKeys saved in first Step Object (in this case "similarartists").
     *                         For toptracks it would be ["artist"]. the values will replace the attribKeys directly in the given array.
     * */
    private String[][] convertJSONStringToArray (String jsonString, String[] firstAttribs, String[] arrayValueAttribs, String[] globalAttributes) {
        Log.d("test", "start");
        String[][] arrayValues = null;
        try {
            Log.d("test", "beginTry");
            JSONObject containerObject = new JSONObject(jsonString).getJSONObject(firstAttribs[0]);
            Log.d("test", "getContainerObject: " + containerObject);
            if (globalAttributes != null) {
                Log.d("test", "getContainerObject: " + containerObject);
                for (int i = 0; i < globalAttributes.length; i++) {
                    globalAttributes[i] = containerObject.getString(globalAttributes[i]);
                }
            }
            JSONArray elementsJsonArray;
            if (containerObject.get(firstAttribs[1]) instanceof JSONArray) {
                elementsJsonArray = containerObject.getJSONArray(firstAttribs[1]);
            } else {
                JSONObject element = containerObject.getJSONObject(firstAttribs[1]);
                elementsJsonArray = new JSONArray().put(element);
            }
            arrayValues = new String[elementsJsonArray.length()][arrayValueAttribs.length];
            for (int elIndex = 0; elIndex < elementsJsonArray.length(); elIndex++) {
                for(int attrIndex = 0; attrIndex < arrayValueAttribs.length; attrIndex++) {
                    arrayValues[elIndex][attrIndex] = arrayValueAttribs[attrIndex];
                }
            }
        } catch (Exception e) {
            Log.e("ERROR while converting element with attribs " + firstAttribs + " JSONString to JSONObject", e.getMessage());
        }
        return arrayValues;
    }
}
