package rest.client;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private ArrayList<Listener> observers;

    public void addPlaylist(final Playlist playlist) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... uri) {
                HttpResponse response;
                String responseString = null;
                try {
                    // serialize playlist
                    String json = gson.toJson(playlist);
                    // build request
                    HttpPost httpPost = new HttpPost(ClientConstants.ADD_PLAYLIST_URL);
                    httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httpPost.setEntity(new StringEntity(json));
                    response = client.execute(httpPost);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems..
                } catch (IOException e) {
                    //TODO Handle problems..
                }
                return responseString;
            }
        }.execute();
    }

    public void findPlaylistByName(final String name) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... uri) {
                HttpResponse response;
                String responseString = null;
                try {
                    String url = String.format(ClientConstants.FIND_PLAYLIST_URL, name);
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setHeader("Accept", "application/json");
                    response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems...
                } catch (IOException e) {
                    //TODO Handle problems...
                }
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
                Playlist playlist = gson.fromJson(result.toString(), Playlist.class);
                findPlaylistCallback(playlist);
            }
        }.execute();
    }

    public void findPlaylistsByGenreAndArtist(final String genre, final String artist) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... uri) {
                HttpResponse response;
                String responseString = null;
                try {
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
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setHeader("Accept", "application/json");
                    response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems...
                } catch (IOException e) {
                    //TODO Handle problems...
                }
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
                Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
                List<Playlist> playlists = gson.fromJson(result.toString(), type);
                findPlaylistsCallback(playlists);
            }
        }.execute();
    }

    public void likePlaylist(final Playlist playlist) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... uri) {
                HttpResponse response;
                String responseString = null;
                try {
                    // serialize playlist
                    String json = gson.toJson(playlist);
                    // build request
                    HttpPut httpPut = new HttpPut(ClientConstants.LIKE_PLAYLIST_URL);
                    httpPut.setHeader(HTTP.CONTENT_TYPE, "application/json");
                    httpPut.setEntity(new StringEntity(json));
                    response = client.execute(httpPut);
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems...
                } catch (IOException e) {
                    //TODO Handle problems...
                }
                return responseString;
            }
        }.execute();
    }

    public void findPlaylistCallback(Playlist playlist) {
        if (observers == null) {
            return;
        }
        for (Listener observer : observers) {
            observer.onFindPlaylistCallback(playlist);
        }
    }

    public void findPlaylistsCallback(List<Playlist> playlists) {
        if (observers == null) {
            return;
        }
        for (Listener observer : observers) {
            observer.onFindPlaylistsCallback(playlists);
        }
    }

    public void addObserver(Listener observer) {
        if(observers == null) {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    public void removeObserver(Listener observer) {
        if(observers == null) {
            return;
        }
        observers.remove(observer);
    }

    public interface Listener {
        void onFindPlaylistCallback(Playlist playlist);
        void onFindPlaylistsCallback(List<Playlist> playlists);
    }
}
