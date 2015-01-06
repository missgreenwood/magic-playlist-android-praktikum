package controllers.myplaylistsFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import tests.R;

/**
 * Created by judith on 31.12.14.
 */
public class PlaylistsListFragment extends ListFragment {
    private Listener mListener;

    public PlaylistsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_playlists, container, false);
        // Hard coded array of playlists list for testing purposes
        // TODO: call method loadPlaylistsFromDB() from caller MyPlaylistsActivity
        String[] listItems = new String[] { "MyPlaylist1", "MyPlaylist2" , "OtherPlaylist1" , "OtherPlaylist2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.rows, R.id.txtview, listItems);
        // Bind adapter to the ListFragment
        setListAdapter(adapter);
        // Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
        return rootView;
    }

    // Handle Item click event
    public void onListItemClick(ListView l, View view, int position, long id) {
        ViewGroup viewg = (ViewGroup) view;
        TextView tv = (TextView) viewg.findViewById(R.id.txtview);
        String selectedGenre = tv.getText().toString();
        Toast.makeText(getActivity(), selectedGenre, Toast.LENGTH_LONG).show();
        mListener.songClicked(view);
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

    public interface Listener {
        public void songClicked(View view);
    }
}

