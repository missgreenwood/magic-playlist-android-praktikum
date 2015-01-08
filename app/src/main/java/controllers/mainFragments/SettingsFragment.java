package controllers.mainFragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import controllers.MainActivity;
import models.Settings;
import tests.R;

/**
 * Created by judith on 02.01.15.
 */
public class SettingsFragment extends ListFragment {
    private OnWrapperActivate act;
    private OnWrapperDeactivate deact;
    private Settings settings;
    private ArrayList<String> mediaWrappers;
    public SettingsFragment() {

    }

    public void setActivateListener(OnWrapperActivate act) {
        this.act = act;
    }

    public void setDeactivateListener(OnWrapperDeactivate deact) {
        this.deact = deact;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Settings");
        settings = Settings.getInstance();
        mediaWrappers = settings.getMediaWrappers();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.rows,R.id.txtview,mediaWrappers);
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
        // Pass string selectedGenre to GeneratorActivity
        // dataPasser.onWrapperPass(wrapperActivated);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        act = null;
        deact = null;
    }


    // Declare interface to pass an activated media wrapper as wrapperActivated to MainActivity
    public interface OnWrapperActivate {
        public void onWrapperActivate(String data);
    }

    // Declare interface to pass a deactivated media wrapper as wrapperDeactivated to MainActivity
    public interface OnWrapperDeactivate {
        public void onWrapperDeactivate(String data);
    }
}
