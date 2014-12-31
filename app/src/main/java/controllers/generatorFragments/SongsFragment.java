package controllers.generatorFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tests.R;

/**
 * Created by judith on 31.12.14.
 */
public class SongsFragment extends Fragment {

    private Listener mListener;
    private OnDataPass dataPasser;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
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
        public void songsClicked(View view);
    }

    // Declare interface to pass string song to GeneratorActivity
    public interface OnDataPass {
        public void onDataPass(String data);
    }
}
