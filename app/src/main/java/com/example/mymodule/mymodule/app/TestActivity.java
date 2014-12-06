package com.example.mymodule.mymodule.app;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mymodule.mediawrappers.LocalFileStreamingMediaWrapper;
import com.example.mymodule.mediawrappers.SoundCloudStreamingMediaWrapper;
import com.example.mymodule.mediawrappers.SpotifyMediaWrapper;

import org.json.JSONException;


public class TestActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        SoundCloudStreamingMediaWrapper sw = new SoundCloudStreamingMediaWrapper(this, new Song("some artistist","Paranoid Android"));

           sw.playSong();




        /*
        Song testSong = new Song("Radiohead", "Videotape");
        LocalFileStreamingMediaWrapper testLocalWrapper = new LocalFileStreamingMediaWrapper(this, testSong);
        testLocalWrapper.playSong();
        */
        //Toast.makeText(this, "Pfad: "+path, Toast.LENGTH_LONG).show();
        //Log.d("", "playpath: " + path);




        //Geht auch! Song per Drag and Drop in Genymotion ziehen, während Genymotion läuft

        String songpath = Environment.getExternalStorageDirectory().getPath() + "/Download/song.mp3";

        //Geht beides!

        //String songpath="https://api.soundcloud.com/tracks/41772991/stream?client_id=9998e443138603b1b6be051350158448";
        // String songpath = "http://freedownloads.last.fm/download/384950466/Thirteen+Thirtyfive.mp3";
        //RemoteStreamingMediaWrapper remoteStreamingMediaWrapper = new RemoteStreamingMediaWrapper(this, songpath);
        //remoteStreamingMediaWrapper.play();
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
}
