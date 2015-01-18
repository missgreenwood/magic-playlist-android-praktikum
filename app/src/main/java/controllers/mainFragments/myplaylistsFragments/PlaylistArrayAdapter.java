package controllers.mainFragments.myplaylistsFragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import models.mediaModels.Playlist;
import tests.R;

/**
 * Created by TheDaAndy on 18.01.2015.
 *
 */
public class PlaylistArrayAdapter extends ArrayAdapter<Playlist>
{

    public PlaylistArrayAdapter(Context context, int resource, int textViewResourceId, List<Playlist> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public void sort(Comparator<? super Playlist> comparator) {
        super.sort(comparator);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout view = (LinearLayout) super.getView(position, convertView, parent);
        view.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,100));
        TextView textView = (TextView)view.findViewById(R.id.txtview);
        textView.setText(getItem(position).getName());
        return view;
    }
}
