package controllers.mainFragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import controllers.mainFragments.myplaylistsFragments.MediaPlayerFragment;
import models.mediaModels.Playlist;
import models.mediawrappers.PlayQueue;
import models.playlist.PlaylistsManager;
import tests.R;

/**
 * Created by judith on 31.12.14.
 */
public class MyPlaylistsFragment extends ListFragment {

    private ArrayList<Playlist> playlists;
    private MediaPlayerFragment mediaPlayerListFragment;

    public MyPlaylistsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_playlists, container, false);
        // Hard coded string array for testing
        // TODO: call method loadPlaylistsFromDB() from caller MyPlaylistsActivity
        playlists = PlaylistsManager.getInstance().getPlaylists();
        int length = playlists.size();
        String[] listItems = new String[length];
        for (int i = 0; i < length; i++) {
            listItems[i] = playlists.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.rows, R.id.txtview, listItems);
        // Bind adapter to the ListFragment
        setListAdapter(adapter);
        // Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
        return rootView;
    }

    // Handle Item click event
    public void onListItemClick(ListView l, View view, int position, long id) {
        playlistClicked(position);
    }

    public void playlistClicked(int playlistId) {
        FragmentActivity owner = this.getActivity();
        mediaPlayerListFragment = (MediaPlayerFragment) owner.getSupportFragmentManager().findFragmentByTag("MediaPlayerFragment");
        if (mediaPlayerListFragment == null) {
            mediaPlayerListFragment = new MediaPlayerFragment();
            FragmentTransaction transact = owner.getSupportFragmentManager().beginTransaction();
            transact.add(android.R.id.content, mediaPlayerListFragment, "MediaPlayerFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
        PlayQueue queue = new PlayQueue(this.getActivity().getApplicationContext(), playlists.get(playlistId).getSongsList());
        mediaPlayerListFragment.setPlayQueue(queue);
        queue.initializePlaylist(true);
    }
}

