package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.playback.Config;

import tests.R;

/**
 * Created by judith on 02.02.15.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button myPlaylists;
    private Button playlistsGenerator;
    private Button otherPlaylists;
    private Button settings;
    private Config spotifyConfig;
    private MyBroadcastReceiver broadcastReceiver = null;

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
        setContentView(R.layout.activity_main);
        myPlaylists = (Button) this.findViewById(R.id.button5);
        playlistsGenerator = (Button) this.findViewById(R.id.button6);
        otherPlaylists = (Button) this.findViewById(R.id.button7);
        settings = (Button) this.findViewById(R.id.button8);
        myPlaylists.setOnClickListener(this);
        playlistsGenerator.setOnClickListener(this);
        otherPlaylists.setOnClickListener(this);
        settings.setOnClickListener(this);
        String songpath = Environment.getExternalStorageDirectory().getPath() + "/Download/song.mp3";
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
        Log.d("", "Klick auf Button");

        if (view == myPlaylists) {
            this.openMyPlaylists();
        }
            /*
        } else if (view == playlistsGenerator) {
            this.openPlaylistGenerator();

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

    public void openMyPlaylists() {
       Intent intent = new Intent(this, MyPlaylistsActivity.class);
       startActivity(intent);
   }

    /* public void openPlaylistBrowser() {
        Intent intent = new Intent(this, BrowserActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    } */
}
