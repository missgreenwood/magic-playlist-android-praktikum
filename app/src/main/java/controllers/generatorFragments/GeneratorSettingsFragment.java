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
public class GeneratorSettingsFragment extends Fragment {

    Listener listener = null;

    public GeneratorSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generator_settings, container, false);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void fireButtonClickedEvent(View view) {
        if (listener != null) {
            listener.buttonClicked(view);
        }
    }

    public interface Listener {
        public void buttonClicked(View view);
    }

}