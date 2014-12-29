package controllers.generatorFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tests.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    Listener listener = null;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void fireButtonClickedEvent(View view) {
        if (listener != null) {
            listener.buttonClicked(view);
        }
    }

    public void testStartClicked(View view) {
        fireButtonClickedEvent(view);
    }

    public interface Listener {
        public void buttonClicked(View view);
    }

}
