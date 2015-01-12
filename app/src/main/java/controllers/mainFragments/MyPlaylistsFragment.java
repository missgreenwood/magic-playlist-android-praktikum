package controllers.mainFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import controllers.MainActivity;
import controllers.mainFragments.generatorFragments.PlaylistFragment;
import models.mediaModels.Playlist;
import models.playlist.PlaylistsManager;
import tests.R;

/**
 * Created by judith on 31.12.14.
 */
public class MyPlaylistsFragment extends ListFragment implements AdapterView.OnItemLongClickListener, PlaylistsManager.Listener {

    private PlaylistFragment playlistFragment;

    public MyPlaylistsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_playlists, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("My Playlists");

        // Bind adapter to the ListFragment
        setListAdapter(
                new PlaylistArrayAdapter(
                        getActivity(),
                        R.layout.rows,
                        R.id.txtview,
                        PlaylistsManager.getInstance().getPlaylists()
                        )
        );
        ((PlaylistArrayAdapter)getListAdapter()).sort(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist lhs, Playlist rhs) {
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        });
        // Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);

        PlaylistsManager.getInstance().addObserver(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("My Playlists");
    }

    @Override
    public void onDestroy() {
        PlaylistsManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    // Handle Item click event
    public void onListItemClick(ListView l, View view, int position, long id) {
        playlistClicked(position);
    }

    public void playlistClicked(int playlistId) {
        playlistFragment = (PlaylistFragment) getActivity().getSupportFragmentManager().findFragmentByTag("MediaPlayerFragment");
        if (playlistFragment == null) {
            playlistFragment = new PlaylistFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, playlistFragment, "MediaPlayerFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
        playlistFragment.setPlaylist((Playlist)getListAdapter().getItem(playlistId));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Playlist playlist = (Playlist)getListAdapter().getItem(position);
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setTitle("delete song from playlist?");
        deleteDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlaylistsManager.getInstance().removePlaylist(playlist);
                onPlaylistsListChange();
            }
        });
        deleteDialog.setNegativeButton("no", null);
        deleteDialog.setMessage("Are you shure you want to delete playlist \"" + playlist.getName() + "\"?");
        deleteDialog.create().show();
        return true;
    }

    @Override
    public void onPlaylistsListChange() {
        ((ArrayAdapter<Playlist>)getListAdapter()).notifyDataSetChanged();
    }

    private class PlaylistArrayAdapter extends ArrayAdapter<Playlist>
    {

        public PlaylistArrayAdapter(Context context, int resource, int textViewResourceId, List<Playlist> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public void sort(Comparator<? super Playlist> comparator) {
            super.sort(comparator);
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

