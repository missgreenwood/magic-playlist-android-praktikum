package controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import controllers.myplaylistsFragments.PlaylistsListFragment;
import models.mediaModels.Playlist;
import models.playlist.PlaylistsManager;
import tests.R;

/**
 * Created by judith on 27.12.14.
 */
public class MyPlaylistsActivity extends ActionBarActivity implements
    PlaylistsListFragment.Listener {

    private PlaylistsListFragment playlistsListFragment;
    private TestActivity player;
    private FragmentManager fragmentManager;
    private PlaylistsManager manager;
    private ArrayList<Playlist> myPlaylists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplaylists);
        fragmentManager = getSupportFragmentManager();
        manager = new PlaylistsManager();
        loadPlaylistsListView();
    }

    private void loadPlaylistsListView() {
        playlistsListFragment = (PlaylistsListFragment) fragmentManager.findFragmentByTag("playlistsListFragment");
        if (playlistsListFragment == null) {
            playlistsListFragment = new PlaylistsListFragment();
            FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
            transact.add(android.R.id.content, playlistsListFragment, "playlistsListFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    @Override
    public void playlistClicked(View view) {
        Log.d("", "Klick auf Button");
        Intent intent = new Intent(this, MediaPlayer.class);
        startActivity(intent);
    }
}
