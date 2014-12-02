package com.example.mymodule.mymodule.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mymodule.mediawrappers.LocalFileStreamingMediaWrapper;
import com.example.mymodule.mediawrappers.RemoteStreamingMediaWrapper;


public class TestActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String songpath = "http://freedownloads.last.fm/download/384950466/Thirteen+Thirtyfive.mp3";
        RemoteStreamingMediaWrapper remoteStreamingMediaWrapper = new RemoteStreamingMediaWrapper(this, songpath);
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
