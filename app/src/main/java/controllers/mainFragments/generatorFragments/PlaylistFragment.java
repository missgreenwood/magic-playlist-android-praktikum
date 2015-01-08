package controllers.mainFragments.generatorFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import tests.R;

/**
 * created by Andreas
 */
public class PlaylistFragment extends ListFragment implements
        PlayQueue.Listener, AdapterView.OnItemLongClickListener, Playlist.Listener, AbsListView.OnScrollListener {

    private Playlist playlist;
    private View currentlyPlayingView;
    private int firstVisibleItem;
    private int visibleItemCount;

    public PlaylistFragment() {
    }

    public int getFirstVisibleItem() {
        return firstVisibleItem;
    }

    public void setFirstVisibleItem(int firstVisibleItem) {
        this.firstVisibleItem = firstVisibleItem;
    }

    public int getVisibleItemCount() {
        return visibleItemCount;
    }

    public void setVisibleItemCount(int visibleItemCount) {
        this.visibleItemCount = visibleItemCount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playlist == null) {
            playlist = new Playlist();
        }
        setListAdapter(new SongArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playlist.getSongsList(true)));

        PlayQueue.getInstance().addObserver(this);

    }

    private void markAsCurrentlyPlaying(int index) {
        removeCurrentlyPlayingMark();
        Log.d("", "mark as currently playing index: " + index);
        Log.d("", "get list view: " + getListView().toString());
        Log.d("", "number of elements: " + getListView().getAdapter().getCount());
        Log.d("", "first visible position: " + firstVisibleItem + "visible item count: " + visibleItemCount);
        int correctedIndex = index - firstVisibleItem;
        Log.d("", " corrected index (at least when ignoring partial visibility: " + correctedIndex);
        currentlyPlayingView = getListView().getChildAt(correctedIndex);
        Log.d("", "currently playing view: " + currentlyPlayingView.toString());
        currentlyPlayingView.setBackgroundColor(Color.argb(100, 80, 80, 80));
    }

    private void removeCurrentlyPlayingMark() {
        if (currentlyPlayingView != null) {
            currentlyPlayingView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(this);
        getListView().setOnScrollListener(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        PlayQueue.getInstance().removeObserver(this);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (v.isEnabled()) {
            if (PlayQueue.getInstance().getCurrentPlaylist() != playlist) {
                PlayQueue.getInstance().importPlaylist(playlist);
            }
            PlayQueue.getInstance().jumpToTrack(position);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Song song = (Song)getListAdapter().getItem(position);
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setTitle("delete song from playlist?");
        deleteDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playlist.removeSong(song);
            }
        });
        deleteDialog.setNegativeButton("no", null);
        deleteDialog.setMessage("Are you shure you want to delete \"" + song.getArtist() + " - " + song.getSongname() + "\" from your Playlist \"" + playlist.getName() + "\"?");
        deleteDialog.create().show();
        return true;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        playlist.addObserver(this);
    }

    private int getSongIndex (Song song)
    {
        Playlist currentPlaylist = PlayQueue.getInstance().getCurrentPlaylist();
        if (currentPlaylist == playlist) {
            return playlist.getSongsList().indexOf(song);
        } else {
            return -1;
        }
    }

    @Override
    public void onNewSongPlaying(Song song) {
        int index = getSongIndex(song);
        if (index != -1) {
            markAsCurrentlyPlaying(index);
        } else {
            removeCurrentlyPlayingMark();
        }
    }

    @Override
    public void onCannotInitializeSong(Song song) {
        int index = getSongIndex(song);
        TextView v = (TextView) getListView().getChildAt(index);
        v.setEnabled(false);
        v.setText(v.getText() + " (not found!)");
        v.setTextColor(Color.rgb(100, 100, 100));
        v.setPaintFlags(v.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onPlaylistChange() {
        ((ArrayAdapter<Song>)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        this.visibleItemCount = visibleItemCount;
        this.firstVisibleItem = firstVisibleItem;

        Log.v("", "on scroll: visibleItemCount: " + visibleItemCount + " firstVisibleItem: " + firstVisibleItem);
    }

    private class SongArrayAdapter extends ArrayAdapter<Song>
    {

        public SongArrayAdapter(Context context, int resource, int textViewResourceId, List<Song> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final Song song = getItem(position);
            TextView view = (TextView) super.getView(position, null, parent);
            if (song.isNotPlayable()) {
                view.setEnabled(false);
                view.setText(view.getText() + " (not found!)");
                view.setTextColor(Color.rgb(100, 100, 100));
                view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (PlayQueue.getInstance().getCurrentSong() == song) {
                currentlyPlayingView = view;
                currentlyPlayingView.setBackgroundColor(Color.argb(100, 80, 80, 80));
            }
            view.setText((position + 1) + ". " + song.getArtist() + " - " + song.getSongname());
            return view;
        }
    }
}
