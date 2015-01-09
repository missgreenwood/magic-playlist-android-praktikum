package controllers.mainFragments.generatorFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import controllers.MainActivity;
import tests.R;

/**
 * Created by judith on 30.12.14.
 */
public class ArtistsFragment extends Fragment {

    private OnArtistPass dataPasser;
    private EditText editArtist;
    private String editedArtist;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    public void setListener(OnArtistPass dataPasser) {
        this.dataPasser = dataPasser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Artists");
        // Get edittext component
        editArtist = (EditText) view.findViewById(R.id.editArtist);
        addKeyListener();
        // Add key listener to keep track of user input
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dataPasser = null;
    }

    public void addKeyListener() {
        editArtist.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Display edited artist in toast message
                    editedArtist = editArtist.getText().toString().trim();
                    Toast.makeText(getActivity(), editedArtist, Toast.LENGTH_LONG).show();
                    // Pass string editedArtist to GeneratorActivity
                    dataPasser.onArtistPass(editedArtist);
                    return true;
                }
                return false;
            }
        });
    }

    // Declare interface to pass string artist to GeneratorActivity
    public interface OnArtistPass {
        public void onArtistPass(String data);
    }

}
