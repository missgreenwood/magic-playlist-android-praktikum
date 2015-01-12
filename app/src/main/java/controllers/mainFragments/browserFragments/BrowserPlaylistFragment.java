package controllers.mainFragments.browserFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import controllers.MainActivity;
import models.mediaModels.Playlist;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import tests.R;

/**
 * Created by judith on 11.01.15.
 */
public class BrowserPlaylistFragment extends ListFragment implements
        PlaylistsManager.Listener,
        Client.Listener {
    private Client client;
    private ArrayList<Playlist> searchResults;

    // test, remove before deploy
    private ArrayList<Playlist> testLists;

    private ResultPlaylistFragment results;
    private String artist;
    private String genre;
    private PlaylistsManager manager;
    private ProgressDialog loadingDialog;
    public BrowserPlaylistFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_results, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Search Results");
        client = Client.getInstance();
        manager = PlaylistsManager.getInstance();
        client.addObserver(this);
        manager.addObserver(this);
        searchResults = new ArrayList<>();

        // test functions, remove when server is fully working
        testLists = PlaylistsManager.getInstance().getPlaylists();
        TreeSet<Playlist> treeSet = new TreeSet<>(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist lhs, Playlist rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        treeSet.addAll(PlaylistsManager.getInstance().getPlaylists());
        Iterator<Playlist> it = treeSet.iterator();
        while (it.hasNext()) {
            testLists.add(it.next());
        }
        Log.d("", "Test lists for upload:" + testLists);
        for (int i = 0; i < testLists.size(); i++) {
            Playlist testList = testLists.get(i);
            client.addPlaylist(testList);
            Log.d("", "Playlist uploaded:" + testList);
        }
        setListAdapter(new PlaylistArrayAdapter(getActivity(),R.layout.rows,R.id.txtview,testLists));

        // remove comment when server is fully working
        // setListAdapter(new PlaylistArrayAdapter(getActivity(),R.layout.rows,R.id.txtview,searchResults));

        setRetainInstance(true);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove comment when server is fully working
        // setLoading(true);
        // client.findPlaylistsByGenreAndArtist(genre, artist);
    }

    @Override
    public void onPlaylistsListChange() {
        ((ArrayAdapter<Playlist>)getListAdapter()).notifyDataSetChanged();
    }

    public void setSearchData(Bundle args) {
        Log.d("", "Search data: " + args);
        this.genre = args.getString("genre");
        this.artist = args.getString("artist");
    }

    @Override
    public void onFindPlaylistCallback(Playlist playlist) {

    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = ProgressDialog.show(getActivity(), "Loading", "Loading matching playlists from database");
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onFindPlaylistsCallback(List<Playlist> playlists) {
        // remove comment when server is fully working
        /* setLoading(false);
            TreeSet<Playlist> treeSet = new TreeSet<>(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist lhs, Playlist rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        // treeSet.addAll(PlaylistsManager.getInstance().getPlaylists());
        Iterator<Playlist> it = treeSet.iterator();
        while (it.hasNext()) {
            searchResults.add(it.next());
        } */
    }

    private class PlaylistArrayAdapter extends ArrayAdapter<Playlist> {
        public PlaylistArrayAdapter(Context context, int resource, int textViewResourceId, List<Playlist> objects) {
            super(context, resource, textViewResourceId, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LinearLayout view = (LinearLayout) super.getView(position, null, parent);
            TextView textView = (TextView)view.findViewById(R.id.txtview);
            textView.setText(getItem(position).getName());
            return view;
        }
    }
}

// TODO: Fix playlist upload
// TODO: Substitute test functions with actual functions


