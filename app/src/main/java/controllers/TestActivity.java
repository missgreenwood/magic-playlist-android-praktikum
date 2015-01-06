package controllers;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.mediawrappers.FileStreamingMediaService;
import models.mediawrappers.PlayQueue;
import tests.R;

import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Config;

import java.util.ArrayList;


public class TestActivity extends ActionBarActivity implements View.OnClickListener {

    //public static MediaPlayer mediaPlayer = new MediaPlayer();
    private Button nextButton;
    private Button beforeButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button generatePlaylistButton;
    private PlayQueue playQueue;
    private ArrayList<Song> songs; //TODO: wieder lokale Variable, ist nur wegen der Testklassen
    private Config spotifyConfig;
    private MyBroadcastReceiver broadcastReceiver = null;

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
        generatePlaylistButton = (Button) this.findViewById(R.id.generatePlaylistButton);
        nextButton.setOnClickListener(this);
        beforeButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);
        generatePlaylistButton.setOnClickListener(this);


//        LastfmMetadataWrapper metadataWrapper = new LastfmMetadataWrapper();
//        metadataWrapper.findSimilarArtists("Radiohead", 5);

        // SoundCloudStreamingMediaWrapper sw = new SoundCloudStreamingMediaWrapper(this, new Song("some artistist","Paranoid Android"));

        //  sw.lookForSong();


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
        if (playQueue != null) {

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

            } else if (view == generatePlaylistButton) {
                this.openPlaylistGenerator();
            }
        }
    }

    public void openPlaylistGenerator() {
        Intent intent = new Intent(this, GeneratorActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileStreamingMediaService.TRACK_FINISHED);
        intentFilter.addAction(PlayQueue.SONG_AVAILABLE);
        intentFilter.addAction(PlayQueue.SONG_NOT_AVAILABLE);
        broadcastReceiver = new MyBroadcastReceiver();
        this.registerReceiver(broadcastReceiver, intentFilter);


        Song strokes = new Song("The Strokes", "Last Nite");
        Song random = new Song("Caribou", "Melody Day");
        Song random2 = new Song("The Strokes", "Reptilia");
        Song newSong = new Song("Tocotronic", "Let there be rock");
        Song fifthSong = new Song("Muse", "Hysteria");


        Playlist testListe = new Playlist();
        testListe.addSong(strokes);
        testListe.addSong(random);
        testListe.addSong(random2);
        testListe.addSong(newSong);
        testListe.addSong(fifthSong);


        playQueue = new PlayQueue(this, testListe.getSongsList());
        playQueue.playSongs();


        super.onStart();


    }


    @Override
    protected void onStop() {

        unregisterReceiver(broadcastReceiver);

        super.onStop();


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
            //  Config playerConfig = new Config(this, response.getAccessToken(), SpotifyMediaWrapper.CLIENT_ID);
            //  setSpotifyConfig(playerConfig);

            Log.d("", "irgendwas config: " + getSpotifyConfig().oauthToken);


        }


    }


}