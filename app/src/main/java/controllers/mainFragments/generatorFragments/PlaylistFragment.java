package controllers.mainFragments.generatorFragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import models.mediaModels.Playlist;
import models.mediaModels.Song;

/**
 * created by Andreas
 */
public class PlaylistFragment extends ListFragment {

    private Listener mListener;
    private Playlist playlist;

    public PlaylistFragment() {
    }

    public void addPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playlist == null) {
            playlist = new Playlist();
        }
        setListAdapter(new ArrayAdapter<Song>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playlist.getSongsList()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onTrackClick(position);
        }
    }

    public interface Listener {
        public void onTrackClick(int songNumber);
    }

}