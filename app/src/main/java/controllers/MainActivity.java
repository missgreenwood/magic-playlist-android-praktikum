package controllers;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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

import java.util.ArrayList;

import controllers.mainFragments.BrowserFragment;
import controllers.mainFragments.GeneratorFragment;
import controllers.mainFragments.MyPlaylistsFragment;
import controllers.mainFragments.SettingsFragment;
import models.Settings;
import models.mediawrappers.FileStreamingMediaService;
import models.mediawrappers.PlayQueue;
import models.playlist.LocalSongsManager;
import models.mediawrappers.SpotifyLoginHandler;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import tests.R;

/**
 * Created by judith on 02.02.15.
 */
public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private static boolean hasAlreadySentRequest = false;
    private Config spotifyConfig;
    private MyBroadcastReceiver broadcastReceiver = null;

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

        loadInitFragment();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        PlayQueue.getInstance().setContext(this);
    }

    private void loadInitFragment() {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragment");
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, mainFragment, "mainFragment");
            transact.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileStreamingMediaService.TRACK_FINISHED);
        intentFilter.addAction(PlayQueue.SONG_AVAILABLE);
        intentFilter.addAction(PlayQueue.SONG_NOT_AVAILABLE);
        broadcastReceiver = new MyBroadcastReceiver();
        this.registerReceiver(broadcastReceiver, intentFilter);
        this.setTitle("Magic Playlist");





        Settings.getInstance().loadSettings(getPreferences(MODE_PRIVATE));
        Settings.getInstance().setOnMediaWrapperListChangeListener(new Settings.Listener() {
            @Override
            public void onMediaWrapperListChange(ArrayList<String> mediaWrappers) {
                PlayQueue.getInstance().setMediaWrappers(mediaWrappers);
            }
        });

        PlayQueue.getInstance().setMediaWrappers(Settings.getInstance().getMediaWrappers());
        PlayQueue.getInstance().setAutoPilotMode(false);

        Client.getInstance().setContext(getApplicationContext());

        LocalSongsManager.getInstance().setContext(getApplicationContext());

        PlaylistsManager.getInstance().loadPlaylists();

        super.onStart();
    }

    @Override
    public void onResume() {
        PlayQueue.getInstance().setAutoPilotMode(false);
        super.onResume();
        this.setTitle("Magic Playlist");
    }

    @Override
    protected void onStop() {
        PlayQueue.getInstance().setAutoPilotMode(true);
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals("android.intent.action.VIEW")) {

            Uri uri = intent.getData();
            Log.d("TAG", "action: "+intent.getAction());
            //TODO: intent filter oder sowas
            Log.d("", "spotify auth received");

          if (uri!=null) {
              Log.d("", "spotify auth received, uri not null");

              AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
              String authorizationCode = response.getCode();
              Log.d("", "authorization code: " + authorizationCode);

              //  Config playerConfig = new Config(this, response.getAccessToken(), SpotifyMediaWrapper.CLIENT_ID);
              //  setSpotifyConfig(playerConfig);

              SpotifyLoginHandler.getInstance().getAccessAndRefreshToken(authorizationCode);
          }
        }
    }


}
