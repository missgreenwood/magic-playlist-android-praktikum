package controllers.mainFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
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
import java.util.List;

import models.Settings;
import tests.R;

/**
 * Created by judith on 02.01.15.
 */
public class SettingsFragment extends ListFragment {
    private Settings settings;
    private ArrayList<String> usedMediaWrappers;
    private ArrayList<String> allMediaWrappers;
    private ProgressDialog loadingDialog;

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        settings = Settings.getInstance();
        rootView.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                setLoading(true);
                settings.confirmWrapperChanges(new Settings.finishReinitListener() {
                    @Override
                    public void onFinishReinitPlaylists() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
//                                    setLoading(false);
                                }
                            });
                    }
                });
            }
        });
        usedMediaWrappers = settings.getMediaWrappers();
        allMediaWrappers = settings.getMediaWrappers(true);
        // Bind adapter to the ListFragment
        setListAdapter( new SettingsAdapter(getActivity(),R.layout.rows2,R.id.wrapper_name,allMediaWrappers));
        // Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
        return rootView;
    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = new ProgressDialog(getActivity());
            loadingDialog.setMessage("Initializing play queue");
            loadingDialog.setTitle("Loading");
            loadingDialog.show();
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private OnClickListener priorityDownListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            if (position < allMediaWrappers.size()) {
                String selectedWrapper = allMediaWrappers.get(position);
                if (position != ListView.INVALID_POSITION) {
                    // Downgrade selected wrapper
                    settings.decreaseWrapperPriority(selectedWrapper);
                    ((SettingsAdapter)getListAdapter()).notifyDataSetChanged();
                    Toast.makeText(getActivity(), selectedWrapper + " downgraded!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private OnClickListener priorityUpListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            if (position < allMediaWrappers.size()) {
                String selectedWrapper = allMediaWrappers.get(position);
                if (position != ListView.INVALID_POSITION) {
                    // Upgrade selected wrapper
                    settings.increaseWrapperPriority(selectedWrapper);
                    ((SettingsAdapter) getListAdapter()).notifyDataSetChanged();
                    Toast.makeText(getActivity(), selectedWrapper + " upgraded!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private OnCheckedChangeListener wrapperCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = buttonView != null ? getListView().getPositionForView(buttonView) : ListView.INVALID_POSITION;
            if (position != ListView.INVALID_POSITION) {
                if (isChecked) {
                    // Activate selected wrapper
                    Settings.getInstance().activateWrapper(allMediaWrappers.get(position));
                    Toast.makeText(getActivity(), allMediaWrappers.get(position) + " activated!", Toast.LENGTH_LONG).show();
                } else {
                    // Deactivate selected wrapper
                    Settings.getInstance().deactivateWrapper(allMediaWrappers.get(position));
                    Toast.makeText(getActivity(), allMediaWrappers.get(position) + " deactivated!", Toast.LENGTH_LONG).show();
                }
            }
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
                convertView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,100));
                holder = new SettingsViewHolder();
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.wrapper_name = (TextView) convertView.findViewById(R.id.wrapper_name);
                holder.up_button = (Button) convertView.findViewById(R.id.upBtn);
                holder.down_button = (Button) convertView.findViewById(R.id.downBtn);
                // Initial value should be set before Listener, otherwise unneeded actions are called
                holder.checkbox.setChecked(usedMediaWrappers.contains(this.getItem(position)));
                holder.checkbox.setOnCheckedChangeListener(wrapperCheckedChangeListener);
                final CheckBox checkBox = holder.checkbox;
                holder.wrapper_name.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBox.performClick();
                    }
                });
                holder.up_button.setOnClickListener(priorityUpListener);
                holder.down_button.setOnClickListener(priorityDownListener);
                convertView.setTag(holder);
            }
            else {
                holder = (SettingsViewHolder) convertView.getTag();
                holder.checkbox.setOnCheckedChangeListener(null);
                //Reset checked state, even if loaded from old item (after up-/downgrading item, this has to be done!)
                holder.checkbox.setChecked(usedMediaWrappers.contains(this.getItem(position)));
                holder.checkbox.setOnCheckedChangeListener(wrapperCheckedChangeListener);
            }
            holder.wrapper_name.setText(this.getItem(position));
            return convertView;
        }
    }
}