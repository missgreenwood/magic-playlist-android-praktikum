package controllers;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Config;

import controllers.mainFragments.GeneratorFragment;
import controllers.mainFragments.MyPlaylistsFragment;
import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.mediawrappers.FileStreamingMediaService;
import models.mediawrappers.PlayQueue;
import tests.R;

/**
 * Created by judith on 02.02.15.
 */
public class MainActivity extends ActionBarActivity implements
        View.OnClickListener
{

    private Button myPlaylists;
    private Button playlistsGenerator;
    private Button otherPlaylists;
    private Button settings;
//    private PlayQueue playQueue; //TODO: lokale Variable
//    private ArrayList<Song> songs; //TODO: wieder lokale Variable, ist nur wegen der Testklassen
    private Config spotifyConfig;
    private MyBroadcastReceiver broadcastReceiver = null;
    private MyPlaylistsFragment playlistsListFragment;
    private GeneratorFragment generatorFragment;

//    public ArrayList<Song> getSongs() {
//        return songs;
//    }
//
//    public void setSongs(ArrayList<Song> songs) {
//        this.songs = songs;
//    }

//    public PlayQueue getPlayQueue() {
//        return playQueue;
//    }

//    public void setPlayQueue(PlayQueue playQueue) {
//        this.playQueue = playQueue;
//    }
//
    public Config getSpotifyConfig() {
        return spotifyConfig;
    }
//
    public void setSpotifyConfig(Config spotifyConfig) {
        Log.v("", "set spotify config");
        this.spotifyConfig = spotifyConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myPlaylists = (Button) this.findViewById(R.id.button5);
        playlistsGenerator = (Button) this.findViewById(R.id.button6);
        otherPlaylists = (Button) this.findViewById(R.id.button7);
        settings = (Button) this.findViewById(R.id.button8);
        myPlaylists.setOnClickListener(this);
        playlistsGenerator.setOnClickListener(this);
        otherPlaylists.setOnClickListener(this);
        settings.setOnClickListener(this);

//        String songpath = Environment.getExternalStorageDirectory().getPath() + "/Download/song.mp3";

        PlayQueue.getInstance().setContext(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        //Log.d("", "Klick auf Button");

        if (view == myPlaylists) {
            this.openMyPlaylists();
        } else if (view == playlistsGenerator) {
            this.openPlaylistGenerator();
        }
        /*
        } else if (view == otherPlaylists) {
            this.openPlaylistBrowser();

        } else if (view == settings) {
            this.openSettings();
        } */
    }

    /* public void openPlaylistGenerator() {
        Intent intent = new Intent(this, GeneratorActivity.class);
        startActivity(intent);
    } */

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileStreamingMediaService.TRACK_FINISHED);
        intentFilter.addAction(PlayQueue.SONG_AVAILABLE);
        intentFilter.addAction(PlayQueue.SONG_NOT_AVAILABLE);
        broadcastReceiver = new MyBroadcastReceiver();
        this.registerReceiver(broadcastReceiver, intentFilter);
        this.setTitle("Magic Playlist");

        Song strokes = new Song("The Strokes", "Last Nite");
        Song random = new Song("Caribou", "Melody Day");
        Song random2 = new Song("The Strokes", "Reptilia");
        Song newSong = new Song("Tocotronic", "Let there be rock");
        Song fifthSong = new Song("Muse", "Hysteria");

        Playlist testListe = new Playlist("testListe");
        testListe.addSong(strokes);
        testListe.addSong(random);
        testListe.addSong(random2);
        testListe.addSong(newSong);
        testListe.addSong(fifthSong);

//        PlaylistsManager.getInstance().addPlaylist(testListe);

//        playQueue = new PlayQueue(this, testListe.getSongsList());
        /* we call playSongs with true here i.e. the mediswrappers will all be overwritten!
        set to false if you don't want media wrappers to be overwritten if they are not null
         */
//        playQueue.playSongs(true);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setTitle("Magic Playlist");
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    public void openMyPlaylists() {
        playlistsListFragment = (MyPlaylistsFragment) getSupportFragmentManager().findFragmentByTag("playlistsListFragment");
        if (playlistsListFragment == null) {
            playlistsListFragment = new MyPlaylistsFragment();
            FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
            transact.add(android.R.id.content, playlistsListFragment, "playlistsListFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
   }

    public void openPlaylistGenerator() {
        generatorFragment = (GeneratorFragment) getSupportFragmentManager().findFragmentByTag("generatorFragment");
        if (generatorFragment == null) {
            generatorFragment = new GeneratorFragment();
            FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
            transact.add(android.R.id.content, generatorFragment, "generatorFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    /* public void openPlaylistBrowser() {
        Intent intent = new Intent(this, BrowserActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    } */


    /* public void setActionBarTitle (String title) {
        getSupportActionBar().setTitle(title);
    } */

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
