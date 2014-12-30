package controllers.generatorFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import tests.R;

public class GenresListFragment extends ListFragment {

    private Listener mListener;

    public GenresListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_genres, container, false);
        String[] selectedItems = new String[] { "Alternative", "Blues" , "Classical" , "Country" , "Dance" , "Electronic" , "Hip-Hop" , "Indie" , "Industrial" , "Instrumental" , "Jazz" , "Pop" , "R&B" , "Soul" , "Rap" , "Reggae" , "Rock" };
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.rows,R.id.txtview,selectedItems);
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
        Toast.makeText(getActivity(), tv.getText().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface Listener {
        public void genresClicked(View view);
    }

}
