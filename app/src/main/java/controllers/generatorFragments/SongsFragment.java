package controllers.generatorFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import tests.R;

/**
 * Created by judith on 31.12.14.
 */
public class SongsFragment extends Fragment {

    private Listener mListener;
    private OnSongPass dataPasser;
    private EditText editSong;
    private String enteredSong;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        // Get edittext component
        editSong = (EditText) view.findViewById(R.id.editSong);
        addKeyListener();
        // Add key listener to keep track of user input
        mListener.songsClicked(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
            dataPasser = (OnSongPass) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        dataPasser = null;
    }

    public void addKeyListener() {
        editSong.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Display edited song in toast message
                    enteredSong = editSong.getText().toString();
                    Toast.makeText(getActivity(), enteredSong, Toast.LENGTH_LONG).show();
                    // Pass string enteredSong to GeneratorActivity
                    dataPasser.onSongPass(enteredSong);
                    return true;
                }
                return false;
            }
        });
    }

    public interface Listener {
        public void songsClicked(View view);
    }

    // Declare interface to pass string entered song to GeneratorActivity
    public interface OnSongPass {
        public void onSongPass(String data);
    }
}
