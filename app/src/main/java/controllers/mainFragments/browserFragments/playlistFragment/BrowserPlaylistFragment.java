package controllers.mainFragments.browserFragments.playlistFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import controllers.MainActivity;
import controllers.mainFragments.browserFragments.ResultPlaylistFragment;
import models.mediaModels.Playlist;
import models.playlist.PlaylistsManager;
import tests.R;

/**
 * Created by judith on 11.01.15.
 */
public class BrowserPlaylistFragment extends ListFragment {

    private ArrayList<Playlist> playlists = new ArrayList<>();

    private ResultPlaylistFragment resultsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_results, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Search Results");

        setListAdapter(new PlaylistArrayAdapter(getActivity(),R.layout.rows,R.id.txtview, playlists));

        setRetainInstance(true);
        return rootView;
    }

    // Handle Item click event
    public void onListItemClick(ListView l, View view, int position, long id) {
        playlistClicked(position);
    }

    public void playlistClicked(int playlistId) {
        resultsFragment = (ResultPlaylistFragment) getActivity().getSupportFragmentManager().findFragmentByTag("ResultPlaylistFragment");
        if (resultsFragment == null) {
            resultsFragment = new ResultPlaylistFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, resultsFragment, "ResultPlaylistFragment");
            // transact.replace(R.id.browserMainViewGroup, resultsFragment, "ResultPlaylistFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
        resultsFragment.setPlaylist((Playlist)getListAdapter().getItem(playlistId));
    }

    public void setPlaylists(List<Playlist> playlists) {
        TreeSet<Playlist> treeSet = new TreeSet<>(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist lhs, Playlist rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        treeSet.addAll(playlists);
        for (Playlist playlist : treeSet) {
            //add only extern playlist, if we don't have the same locally! Otherwise, add rating info
            Playlist ownPlaylist = PlaylistsManager.getInstance().getEqualPlaylist(playlist);
            if (ownPlaylist != null) {
                ownPlaylist.setLikes(playlist.getLikes());
                this.playlists.add(ownPlaylist);
            } else {
                this.playlists.add(playlist);
            }
        }
        PlaylistArrayAdapter adapter = (PlaylistArrayAdapter)getListAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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