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
 * Created by judith on 30.12.14.
 */
public class ArtistsFragment extends Fragment {

    private Listener mListener;
    private OnArtistPass dataPasser;
    private EditText editArtist;
    private String enteredArtist;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        // Get edittext component
        editArtist = (EditText) view.findViewById(R.id.editArtist);
        addKeyListener();
        // Add key listener to keep track of user input
        mListener.artistsClicked(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
            dataPasser = (OnArtistPass) activity;
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
        editArtist.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Display edited artist in toast message
                    enteredArtist = editArtist.getText().toString();
                    Toast.makeText(getActivity(), enteredArtist, Toast.LENGTH_LONG).show();
                    // Pass string editedArtist to GeneratorActivity
                    dataPasser.onArtistPass(enteredArtist);
                    return true;
                }
                return false;
            }
        });
    }

    public interface Listener {
        public void artistsClicked(View view);
    }

    // Declare interface to pass string artist to GeneratorActivity
    public interface OnArtistPass {
        public void onArtistPass(String data);
    }

}
