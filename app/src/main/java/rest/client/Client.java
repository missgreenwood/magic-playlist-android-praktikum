package rest.client;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import models.mediaModels.Playlist;

public class Client {

    private final static Client instance = new Client();

    public static Client getInstance () {
        return instance;
    }

    private Client(){} //not accessable

    private final Gson gson = new Gson();
    private final HttpClient client = new DefaultHttpClient();

    private Context context;

    private ArrayList<ClientListener> observers = new ArrayList<>();

    public void setContext(Context context) {
        this.context = context;
    }

    public RequestHandle addPlaylist(final ClientListener.AddPlaylistListener listener, final Playlist playlist) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
            String json = gson.toJson(playlist);
            Log.d("Client", "playlist send request to address: \"" + ClientConstants.ADD_PLAYLIST_URL + "\" with playlistString: " + json);

            ByteArrayEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            return asyncHttpClient.post(context, ClientConstants.ADD_PLAYLIST_URL, entity, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Log.e("Client", "Error while uploading Playlist: " + throwable.getMessage() + " headers: " + headers + " status: " + i);
                    if (listener != null) {
                        listener.onAddPlaylistError(i == 409);
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    Log.d("TEST", "success!!!" + " string: " + s + " status: " + i);
                    if (listener != null) {
                        listener.onAddPlaylistSuccess();
                    }
                }
            });
        } catch (Exception e) {
            if (listener != null) {
                listener.onAddPlaylistError(false);
            }
            return null;
        }
    }

    public RequestHandle findPlaylistByName(final ClientListener.FindSinglePlaylistListener listener, final String name) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        String url = String.format(ClientConstants.FIND_PLAYLIST_URL, name);
        return asyncHttpClient.get(context, url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (listener != null) {
                    listener.onFindSinglePlaylistError();
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    Playlist playlist = gson.fromJson(s, Playlist.class);
                    if (listener != null) {
                        listener.onFindSinglePlaylistSuccess(playlist);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFindSinglePlaylistError();
                    }
                }
            }
        });
    }

    public RequestHandle findPlaylistsByGenreAndArtist(final ClientListener.FindPlaylistsListener listener , String genre, String artist) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};

        String url = ClientConstants.FIND_PLAYLISTS_URL;
        if (genre != null && !genre.isEmpty() && artist != null && !artist.isEmpty()) {
            String genreParam = String.format(ClientConstants.GENRE_PARAM, genre);
            String artistParam = String.format(ClientConstants.ARTIST_PARAM, artist);
            url = url + genreParam + "&" + artistParam;
        } else {
            if (genre != null && !genre.isEmpty()) {
                url = url + String.format(ClientConstants.GENRE_PARAM, genre);
            } else {
                url = url + String.format(ClientConstants.ARTIST_PARAM, artist);
            }
        }

        return asyncHttpClient.get(context, url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (listener != null) {
                    listener.onFindPlaylistsError();
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
                    List<Playlist> playlists = gson.fromJson(s, type);
                    if (listener != null) {
                        listener.onFindPlaylistsSuccess(playlists);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFindPlaylistsError();
                    }
                }
            }
        });
    }

    public RequestHandle likePlaylist(final ClientListener.LikePlaylistListener listener, final Playlist playlist) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams("name", playlist.getName());
        params.setContentEncoding("UTF-8");
        return asyncHttpClient.put(context, ClientConstants.LIKE_PLAYLIST_URL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (listener != null) {
                    listener.onLikePlaylistError();
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    playlist.setLikes(Integer.parseInt(s));
                } catch (Exception e) {
                    Log.e("Client", "could not convert likes String \"" + s + "\" to int");
                }
                if (listener != null) {
                    listener.onLikePlaylistSuccess();
                }
            }
        });
    }
}
