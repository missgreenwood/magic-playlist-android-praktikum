package controllers.mainFragments.generatorFragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import models.playlist.LocalSongsManager;
import tests.R;

public class ArtistsFragment extends ListFragment {

    private Listener listener;
    protected ArrayList<String> artists = new ArrayList<>();
    private ArrayList<String> selectedArtists = new ArrayList<>();

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artists.addAll(LocalSongsManager.getInstance().getAllArtists());
        setListAdapter(new ArtistsArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, artists));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_songs, container, false);
        final EditText searchInput = (EditText)v.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void afterTextChanged(Editable s) {
                artists.clear();
                artists.addAll(LocalSongsManager.getInstance().getArtists(searchInput.getText().toString()));
                ((ArtistsArrayAdapter)getListAdapter()).notifyDataSetChanged();
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
            listener.onArtistsSelection(selectedArtists);
        }
        listener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String artist = artists.get(position);
        if (selectedArtists.contains(artist)) {
            selectedArtists.remove(artist);
        } else {
            selectedArtists.add(artist);
        }
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setSelectedArtists(ArrayList<String> selectedArtists) {
        if (selectedArtists != null) {
            this.selectedArtists = selectedArtists;
            ArtistsArrayAdapter adapter = (ArtistsArrayAdapter)getListAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public interface Listener {
        void onArtistsSelection(ArrayList<String> artists);
    }

    private class ArtistsArrayAdapter extends ArrayAdapter<String> {

        public ArtistsArrayAdapter(Activity activity, int rows, int text1, ArrayList<String> artists) {
            super(activity, rows, text1, artists);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (convertView == null) {
                view = new TextView(getContext());
            }
            String artist = getItem(position);
            if (artist != null) {
                view.setText(artist);
            } else {
                view.setText("error");
            }
            if (selectedArtists.contains(artist)) {
                view.setBackgroundColor(Color.argb(100, 100, 100, 100));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }
}
