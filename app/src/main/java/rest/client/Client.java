package rest.client;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

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

    public void addPlaylist(final Playlist playlist) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
            String json = gson.toJson(playlist);
            Log.d("Client", "playlist send request to address: \"" + ClientConstants.ADD_PLAYLIST_URL + "\" with playlistString: " + json);

            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");
            asyncHttpClient.post(context, ClientConstants.ADD_PLAYLIST_URL, entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Log.e("Client", "Error while uploading Playlist: " + throwable.getMessage() + " headers: " + headers + " status: " + i);
                    onAddPlaylistError();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    for (Header header : headers) {
                        Log.d("TEST", "header: " + header);
                    }
                    Log.d("TEST", "success!!!" + " string: " + s + " status: " + i);
                    onAddPlaylistSuccess();
                }
            });
        } catch (Exception e) {
            Log.e("Client", "Error: " + e.getMessage());
            onAddPlaylistError();
        }
    }

    public void findPlaylistByName(final String name) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("Content-type", "application/json")};
        String url = String.format(ClientConstants.FIND_PLAYLIST_URL, name);
        asyncHttpClient.get(context, url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                onFindSinglePlaylistError();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    Playlist playlist = gson.fromJson(s, Playlist.class);
                    onFindSinglePlaylistSuccess(playlist);
                } catch (Exception e) {
                    onFindSinglePlaylistError();
                }
            }
        });
    }

    public void findPlaylistsByGenreAndArtist(String genre, String artist) {
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

        asyncHttpClient.get(context, url, headers, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                onFindPlaylistsError();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
                    List<Playlist> playlists = gson.fromJson(s, type);
                    onFindPlaylistsSuccess(playlists);
                } catch (Exception e) {
                    onFindPlaylistsError();
                }
            }
        });
    }

    public void likePlaylist(final Playlist playlist) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
            String json = gson.toJson(playlist);
            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");
            asyncHttpClient.post(context, ClientConstants.LIKE_PLAYLIST_URL, entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    onLikePlaylistError();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    onLikePlaylistSuccess();
                }
            });
        } catch (Exception e) {
            onLikePlaylistError();
        }
    }

    public void addObserver(ClientListener observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ClientListener observer) {
        observers.remove(observer);
    }

    private void onAddPlaylistSuccess()
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.AddPlaylistListener) {
                ((ClientListener.AddPlaylistListener)observer).onAddPlaylistSuccess();
            }
        }
    }

    private void onAddPlaylistError()
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.AddPlaylistListener) {
                ((ClientListener.AddPlaylistListener)observer).onAddPlaylistError();
            }
        }
    }

    private void onFindSinglePlaylistSuccess(Playlist playlist)
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.FindSinglePlaylistListener) {
                ((ClientListener.FindSinglePlaylistListener)observer).onFindSinglePlaylistSuccess(playlist);
            }
        }
    }

    private void onFindSinglePlaylistError()
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.FindSinglePlaylistListener) {
                ((ClientListener.FindSinglePlaylistListener)observer).onFindSinglePlaylistError();
            }
        }
    }

    private void onFindPlaylistsSuccess(List<Playlist> playlists)
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.FindPlaylistsListener) {
                ((ClientListener.FindPlaylistsListener)observer).onFindPlaylistsSuccess(playlists);
            }
        }
    }

    private void onFindPlaylistsError()
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.FindPlaylistsListener) {
                ((ClientListener.FindPlaylistsListener)observer).onFindPlaylistsError();
            }
        }
    }

    private void onLikePlaylistSuccess()
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.LikePlaylistListener) {
                ((ClientListener.LikePlaylistListener)observer).onLikePlaylistSuccess();
            }
        }
    }

    private  void onLikePlaylistError()
    {
        for (ClientListener observer : observers) {
            if (observer instanceof ClientListener.LikePlaylistListener) {
                ((ClientListener.LikePlaylistListener)observer).onLikePlaylistError();
            }
        }
    }
//    public void addPlaylist(final Playlist playlist) {
//        new AsyncTask<String, Void, String>() {
//
//            @Override
//            protected String doInBackground(String... uri) {
//                HttpResponse response;
//                String responseString = null;
//                try {
//                    // serialize playlist
//                    String json = gson.toJson(playlist);
//                    // build request
//                    HttpPost httpPost = new HttpPost(ClientConstants.ADD_PLAYLIST_URL);
//                    httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
//                    httpPost.setEntity(new StringEntity(json));
//                    response = client.execute(httpPost);
//                    StatusLine statusLine = response.getStatusLine();
//                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        response.getEntity().writeTo(out);
//                        out.close();
//                        responseString = out.toString();
//                    } else {
//                        //Closes the connection.
//                        response.getEntity().getContent().close();
//                        throw new IOException(statusLine.getReasonPhrase());
//                    }
//                } catch (Exception e) {
//                }
//                return responseString;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                for (ClientListener observer : observers) {
//                    if (observer instanceof ClientListener.AddPlaylistListener) {
//                        ((ClientListener.AddPlaylistListener)observer).onAddPlaylistSuccess();
//                    }
//                }
//            }
//        }.execute();
//    }

//    public void findPlaylistByName(final String name) {
//        new AsyncTask<String, Void, String>() {
//
//            @Override
//            protected String doInBackground(String... uri) {
//                HttpResponse response;
//                String responseString = null;
//                try {
//                    String url = String.format(ClientConstants.FIND_PLAYLIST_URL, name);
//                    HttpGet httpGet = new HttpGet(url);
//                    httpGet.setHeader("Accept", "application/json");
//                    response = client.execute(httpGet);
//                    StatusLine statusLine = response.getStatusLine();
//                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        response.getEntity().writeTo(out);
//                        out.close();
//                        responseString = out.toString();
//                    } else {
//                        //Closes the connection.
//                        response.getEntity().getContent().close();
//                        throw new IOException(statusLine.getReasonPhrase());
//                    }
//                } catch (ClientProtocolException e) {
//                } catch (IOException e) {
//                }
//                return responseString;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                Playlist playlist = gson.fromJson(result.toString(), Playlist.class);
//                for (ClientListener observer : observers) {
//                    Class<ClientListener.AddPlaylistListener> playlistListenerClass;
//                    if (observer instanceof ClientListener.AddPlaylistListener) {
//                        ((ClientListener.AddPlaylistListener)observer).onAddPlaylistSuccess();
//                    }
//                }
//                findPlaylistCallback();
//            }
//        }.execute();
//    }

//    public void findPlaylistsByGenreAndArtist(final String genre, final String artist) {
//        new AsyncTask<String, Void, String>() {
//
//            @Override
//            protected String doInBackground(String... uri) {
//                HttpResponse response;
//                String responseString = null;
//                try {
//                    String url = ClientConstants.FIND_PLAYLISTS_URL;
//                    if (genre != null && !genre.isEmpty() && artist != null && !artist.isEmpty()) {
//                        String genreParam = String.format(ClientConstants.GENRE_PARAM, genre);
//                        String artistParam = String.format(ClientConstants.ARTIST_PARAM, artist);
//                        url = url + genreParam + "&" + artistParam;
//                    } else {
//                        if (genre != null && !genre.isEmpty()) {
//                            url = url + String.format(ClientConstants.GENRE_PARAM, genre);
//                        } else {
//                            url = url + String.format(ClientConstants.ARTIST_PARAM, artist);
//                        }
//                    }
//                    HttpGet httpGet = new HttpGet(url);
//                    httpGet.setHeader("Accept", "application/json");
//                    response = client.execute(httpGet);
//                    StatusLine statusLine = response.getStatusLine();
//                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        response.getEntity().writeTo(out);
//                        out.close();
//                        responseString = out.toString();
//                    } else {
//                        //Closes the connection.
//                        response.getEntity().getContent().close();
//                        throw new IOException(statusLine.getReasonPhrase());
//                    }
//                } catch (ClientProtocolException e) {
//                } catch (IOException e) {
//                }
//                return responseString;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
//                List<Playlist> playlists = gson.fromJson(result.toString(), type);
//                findPlaylistsCallback(playlists);
//            }
//        }.execute();
//    }

//    public void likePlaylist(final Playlist playlist) {
//        new AsyncTask<String, Void, String>() {
//
//            @Override
//            protected String doInBackground(String... uri) {
//                HttpResponse response;
//                String responseString = null;
//                try {
//                    // serialize playlist
//                    String json = gson.toJson(playlist);
//                    // build request
//                    HttpPut httpPut = new HttpPut(ClientConstants.LIKE_PLAYLIST_URL);
//                    httpPut.setHeader(HTTP.CONTENT_TYPE, "application/json");
//                    httpPut.setEntity(new StringEntity(json));
//                    response = client.execute(httpPut);
//                    StatusLine statusLine = response.getStatusLine();
//                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        response.getEntity().writeTo(out);
//                        out.close();
//                        responseString = out.toString();
//                    } else {
//                        //Closes the connection.
//                        response.getEntity().getContent().close();
//                        throw new IOException(statusLine.getReasonPhrase());
//                    }
//                } catch (ClientProtocolException e) {
//                } catch (IOException e) {
//                }
//                return responseString;
//            }
//        }.execute();
//    }
}
