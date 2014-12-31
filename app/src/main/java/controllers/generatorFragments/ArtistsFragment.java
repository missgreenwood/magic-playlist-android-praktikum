package controllers.generatorFragments;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tests.R;

/**
 * Created by judith on 30.12.14.
 */
public class ArtistsFragment extends Fragment {

    private Listener mListener;
    private OnDataPass dataPasser;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        // Get edittext component
        /* final EditText editArtist = (EditText) view.findViewById(R.id.editArtist);
        // Add key listener to keep track of user input
        editArtist.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Display edited artist in toast message
                    Toast.makeText(getActivity(), editArtist.getText(), Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        String editedArtist = editArtist.getText().toString();
        mListener.artistsClicked(view);
        // Pass string editedArtist to GeneratorActivity
        dataPasser.onDataPass(editedArtist); */
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
            dataPasser = (OnDataPass) activity;
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

    public interface Listener {
        public void artistsClicked(View view);
    }

    // Declare interface to pass string artist to GeneratorActivity
    public interface OnDataPass {
        public void onDataPass(String data);
    }

}
