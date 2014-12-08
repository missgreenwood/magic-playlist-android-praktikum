package com.example.mymodule.mymodule.app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mymodule.mediawrappers.PlayQueue;
import com.example.mymodule.mediawrappers.SpotifyMediaWrapper;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.Player;

import java.util.ArrayList;
import java.util.List;


public class TestActivity extends ActionBarActivity implements View.OnClickListener {

    //public static MediaPlayer mediaPlayer = new MediaPlayer();
    private Button nextButton;
    private Button beforeButton;
    private Button pauseButton;
    private Button resumeButton;
    private PlayQueue playQueue;
    private ArrayList<Song> songs; //TODO: wieder lokale Variable, ist nur wegen der Testklassen
    private Config spotifyConfig;

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public PlayQueue getPlayQueue() {
        return playQueue;
    }

    public void setPlayQueue(PlayQueue playQueue) {
        this.playQueue = playQueue;
    }

    public Config getSpotifyConfig() {
        return spotifyConfig;
    }

    public void setSpotifyConfig(Config spotifyConfig) {

        Log.d("", "set spotify config");
        this.spotifyConfig = spotifyConfig;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        //  SpotifyAuthentication.openAuthWindow(SpotifyMediaWrapper.CLIENT_ID, "token", SpotifyMediaWrapper.REDIRECT_URI,
        //     new String[]{"user-read-private", "streaming"}, null, this);


        nextButton = (Button) this.findViewById(R.id.button);
        beforeButton = (Button) this.findViewById(R.id.button2);
        pauseButton = (Button) this.findViewById(R.id.button3);
        resumeButton = (Button) this.findViewById(R.id.button4);
        nextButton.setOnClickListener(this);
        beforeButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);


        // SoundCloudStreamingMediaWrapper sw = new SoundCloudStreamingMediaWrapper(this, new Song("some artistist","Paranoid Android"));

        //  sw.lookForSong();

        songs = new ArrayList<Song>();

        //  Song spotify_test=new Song("Radiohead","Paranoid");
        //    spotify_test.setMediaWrapperType(Song.MEDIA_WRAPPER_SPOTIFY);

        Song radiohead = new Song("blub", "Videotape");
        radiohead.setMediaWrapperType(Song.MEDIA_WRAPPER_LOCAL_FILE);
        Song strokes = new Song("The Strokes", "Reptilia");
        strokes.setMediaWrapperType(Song.MEDIA_WRAPPER_LOCAL_FILE);

        Song random = new Song("test", "Codex");
        random.setMediaWrapperType(Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD);

        Song random2 = new Song("test", "Lotus Flower");
        random2.setMediaWrapperType(Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD);

        Log.d("", "call play queue next track");

        //    songs.add(spotify_test);
        songs.add(random);
        songs.add(random2);
        songs.add(strokes);
//
        songs.add(radiohead);
        //   songs.add(radiohead);
        //   songs.add(radiohead);


        playQueue = new PlayQueue(this, songs);
        playQueue.playSongs();


        String songpath = Environment.getExternalStorageDirectory().getPath() + "/Download/song.mp3";


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Log.d("", "Klick auf Button");
        if (view == nextButton) {
            Log.d("", "call play queue next track");
            playQueue.nextTrack();

        } else if (view == beforeButton) {
            Log.d("", "call play queue before track");
            playQueue.beforeTrack();

        } else if (view == pauseButton) {
            Log.d("", "pause button");
            playQueue.pausePlayer();
        } else if (view == resumeButton) {
            playQueue.resumePlayer();

        }
    }


    //TODO: muss das wirklich hier rein?


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        Uri uri = intent.getData();
        //TODO: intent filter oder sowas
        Log.d("", "spotify auth received");

        if (uri != null) {
            Log.d("", "spotify auth received, uri not null");

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Config playerConfig = new Config(this, response.getAccessToken(), SpotifyMediaWrapper.CLIENT_ID);
            setSpotifyConfig(playerConfig);

            Log.d("", "irgendwas config: " + getSpotifyConfig().oauthToken);
            /*Spotify spotify = new Spotify();
            mPlayer = spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addPlayerNotificationCallback(MainActivity.this);
                    mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }*/

        }


    }


}