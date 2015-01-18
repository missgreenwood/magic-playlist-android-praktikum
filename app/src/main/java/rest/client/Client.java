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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import models.mediaModels.Playlist;

/**
 * created by Martin and Andreas
 * */

public class Client {

    private final static Client instance = new Client();

    public static Client getInstance () {
        return instance;
    }

    private Client(){} //not accessable

    private final Gson gson = new Gson();

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public RequestHandle addPlaylist(final Playlist playlist, final ClientListener.AddPlaylistListener listener) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
            String json = gson.toJson(playlist);

            ByteArrayEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            return asyncHttpClient.post(context, ClientConstants.ADD_PLAYLIST_URL, entity, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    if (listener != null) {
                        listener.onAddPlaylistError(i == 409);
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    playlist.setAlreadyUploaded(true);
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

    public RequestHandle findPlaylistByName(final String name, final ClientListener.FindSinglePlaylistListener listener) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        return asyncHttpClient.get(context, ClientConstants.FIND_PLAYLIST_URL, new RequestParams("name", name), new TextHttpResponseHandler() {
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

    public RequestHandle findPlaylistsByGenreAndArtist(String genre, String artist, final ClientListener.FindPlaylistsListener listener) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        RequestParams params = new RequestParams(ClientConstants.GENRE_PARAM, genre);
        params.put(ClientConstants.ARTIST_PARAM, artist);

        return asyncHttpClient.get(context, ClientConstants.FIND_PLAYLISTS_URL, params, new TextHttpResponseHandler() {
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
                } catch (com.google.gson.JsonSyntaxException e) {
                    if (listener != null) {
                        listener.onFindPlaylistsError();
                    }
                }
            }
        });
    }

    public RequestHandle findSimilarPlaylists(Playlist playlist, final ClientListener.FindPlaylistsListener listener) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
            String json = gson.toJson(playlist);

            ByteArrayEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            return asyncHttpClient.post(context, ClientConstants.SEARCH_SIMILAR, entity, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Log.e("Client", "search similar playlists failed: " + s + " status code: " + i);
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
                    } catch (com.google.gson.JsonSyntaxException e) {
                        Log.e("Client", "could not parse playlists to json (search similar)");
                        if (listener != null) {
                            listener.onFindPlaylistsError();
                        }
                    }
                }
            });
        } catch (Exception e) {
            if (listener != null) {
                listener.onFindPlaylistsError();
            }
            return null;
        }
    }

    public RequestHandle likePlaylist(final Playlist playlist, final ClientListener.LikePlaylistListener listener) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        return asyncHttpClient.get(
                context,
                ClientConstants.LIKE_PLAYLIST_URL,
                new RequestParams("name", playlist.getName()),
                new TextHttpResponseHandler()
            {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    if (listener != null) {
                        listener.onLikePlaylistError();
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    try {
                        Playlist persistedPlaylist = gson.fromJson(s, Playlist.class);
                        playlist.setLikes(persistedPlaylist.getLikes());
                        playlist.setAlreadyLiked(true);
                    } catch(Exception e) {}
                    if (listener != null) {
                        listener.onLikePlaylistSuccess();
                    }
                }
            }
        );
    }
}
