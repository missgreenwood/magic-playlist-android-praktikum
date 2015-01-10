package controllers.mainFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controllers.MainActivity;
import models.Settings;
import tests.R;

/**
 * Created by judith on 02.01.15.
 */
public class SettingsFragment extends ListFragment {
    private Settings settings;
    private ArrayList<String> usedMediaWrappers;
    private ArrayList<String> allMediaWrappers;
    private String[] all;
    private String[] used;
    private boolean[] wrapperStates;
    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Settings");
        settings = Settings.getInstance();
        usedMediaWrappers = settings.getMediaWrappers();
        allMediaWrappers = settings.getMediaWrappers(true);
        all = allMediaWrappers.toArray(new String[allMediaWrappers.size()]);
        used = usedMediaWrappers.toArray(new String[usedMediaWrappers.size()]);
        // Bind adapter to the ListFragment
        setListAdapter( new SettingsAdapter(getActivity(),R.layout.rows2,R.id.wrapper_name,allMediaWrappers));
        // Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
        return rootView;
    }

   /* // Handle Item check event
   public void onListItemClick(ListView l, View view, int position, long id) {
       String selectedWrapper = all[position];
        if (settings.isWrapperActive(selectedWrapper)) {
            // Remove deselected wrapper from the list of selected wrappers
            settings.deactivateWrapper(selectedWrapper);
            Toast.makeText(getActivity(), selectedWrapper + " deactivated!", Toast.LENGTH_LONG).show();
        }
        /* else {
            // Add selected wrapper to the list of selected wrappers
            settings.activateWrapper(selectedWrapper);
            Toast.makeText(getActivity(), selectedWrapper + " activated!", Toast.LENGTH_LONG).show();
        }
    } */

    private OnClickListener priorityDownListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            String selectedWrapper = all[position];
            if (position != ListView.INVALID_POSITION) {
                // Downgrade selected wrapper
                settings.decreaseWrapperPriority(selectedWrapper);
                Toast.makeText(getActivity(), selectedWrapper + " downgraded!", Toast.LENGTH_LONG).show();
            }
        }
    };

    private OnClickListener priorityUpListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            String selectedWrapper = all[position];
            if (position != ListView.INVALID_POSITION) {
                // Upgrade selected wrapper
                settings.increaseWrapperPriority(selectedWrapper);
                Toast.makeText(getActivity(), selectedWrapper + " upgraded!", Toast.LENGTH_LONG).show();
            }
        }
    };

    private OnCheckedChangeListener wrapperCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            /* final int position = getListView().getPositionForView(buttonView);
            if (position != ListView.INVALID_POSITION && wrapperStates[position]) {
            } */
        }
    };

    private static class SettingsViewHolder {
        public CheckBox checkbox;
        public TextView wrapper_name;
        public Button up_button;
        public Button down_button;
    }

    private class SettingsAdapter extends ArrayAdapter<String> {

        public SettingsAdapter(Context context, int resource, int textViewResourceId, List<String> wrappers) {
            super(context, resource, textViewResourceId, wrappers);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            SettingsViewHolder holder = null;
            if(convertView == null) {
                Context context = getActivity();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.rows2, parent, false);
                holder = new SettingsViewHolder();
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.wrapper_name = (TextView) convertView.findViewById(R.id.wrapper_name);
                holder.up_button = (Button) convertView.findViewById(R.id.upBtn);
                holder.down_button = (Button) convertView.findViewById(R.id.downBtn);
                holder.checkbox.setOnCheckedChangeListener(wrapperCheckedChangeListener);
                holder.up_button.setOnClickListener(priorityUpListener);
                holder.down_button.setOnClickListener(priorityDownListener);
                convertView.setTag(holder);
            }
            else {
                holder = (SettingsViewHolder) convertView.getTag();
            }
            if (Arrays.asList(used).contains(all[position])) {
                holder.checkbox.setChecked(true);
                }
            holder.wrapper_name.setText(all[position]);
            return convertView;
        }
    }
}