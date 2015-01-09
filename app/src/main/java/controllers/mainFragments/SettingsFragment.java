package controllers.mainFragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import controllers.MainActivity;
import models.Settings;
import tests.R;

/**
 * Created by judith on 02.01.15.
 */
public class SettingsFragment extends ListFragment {
    private Settings settings;
    private ArrayList<String> mediaWrappers;
    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Settings");
        settings = Settings.getInstance();
        mediaWrappers = settings.getMediaWrappers(true);
        // settings.activateWrapper("Spotify");
        // settings.activateWrapper("LocalFile");
        // settings.activateWrapper("SoundCloud");
        // mediaWrappers = new ArrayList<String>(Arrays.asList("Spotify","LocalFile","SoundCloud"));
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.rows_checkable,R.id.txt_title,mediaWrappers);
        // Bind adapter to the ListFragment
        setListAdapter(adapter);
        // Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
        return rootView;
    }

    // Handle Item check event
    public void onListItemClick(ListView l, View view, int position, long id) {
        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        l.setLongClickable(true);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Selected wrapper
                String selectedWrapper = (String) getListAdapter().getItem(position);
                if (settings.isWrapperActive(selectedWrapper)) {
                    // Remove deselected wrapper from the list of selected wrappers
                    settings.deactivateWrapper(selectedWrapper);
                    Toast.makeText(getActivity(), selectedWrapper + " deactivated!", Toast.LENGTH_LONG).show();
                }
                else {
                    // Add selected wrapper to the list of selected wrappers
                    settings.activateWrapper(selectedWrapper);
                    Toast.makeText(getActivity(), selectedWrapper + " activated!", Toast.LENGTH_LONG).show();
                }
           }
        });
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
