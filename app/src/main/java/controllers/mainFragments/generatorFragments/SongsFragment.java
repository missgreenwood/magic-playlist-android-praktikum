package controllers.mainFragments.generatorFragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import models.mediaModels.Song;

import models.playlist.LocalSongsManager;
import tests.R;

public class SongsFragment extends ListFragment {

    private Listener listener;
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Song> selectedSongs = new ArrayList<>();

    public SongsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songs.addAll(LocalSongsManager.getInstance().getAllTracks());
        setListAdapter(new SongArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, songs));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_songs, container, false);
        final EditText searchInput = (EditText)v.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void afterTextChanged(Editable s) {
                songs.clear();
                songs.addAll(LocalSongsManager.getInstance().getFilterTracks(searchInput.getText().toString()));
                ((SongArrayAdapter)getListAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        return v;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (listener != null) {
            listener.onSongsSelection(selectedSongs);
            listener = null;
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Song song = songs.get(position);
        Log.d("SongsFragment", "selected: " + selectedSongs);
        if (selectedSongs.contains(song)) {
            selectedSongs.remove(song);
        } else {
            selectedSongs.add(song);
        }
        Log.d("SongsFragment", "selected afterwards: " + selectedSongs);
        ((SongArrayAdapter)getListAdapter()).notifyDataSetChanged();
    }

    public void setSelectedSongs(ArrayList<Song> selectedSongs) {
        if (selectedSongs != null) {
            this.selectedSongs = selectedSongs;
            SongArrayAdapter adapter = (SongArrayAdapter)getListAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public interface Listener {
        void onSongsSelection(ArrayList<Song> songs);
    }

    private class SongArrayAdapter extends ArrayAdapter<Song> {

        public SongArrayAdapter(Activity activity, int rows, int text1, ArrayList<Song> songs) {
            super(activity, rows, text1, songs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            Song song = getItem(position);
            if (song != null) {
                view.setText(song.getSongname());
            } else {
                view.setText("no song...");
            }
            Log.d("SongsFragment", "rendering song: " + song);
            if (selectedSongs.contains(song)) {
                view.setBackgroundColor(Color.argb(100, 100, 100, 100));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }
}
