package controllers.mainFragments.generatorFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import tests.R;

public class SingleArtistFragment extends ArtistsFragment {

    private Listener listener;
    private EditText searchInput;

    public SingleArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        searchInput = (EditText) v.findViewById(R.id.searchInput);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (listener != null) {
            listener.onSingleArtistSelection(artists.get(position));

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
            getFragmentManager().popBackStack();
        }
    }

    public interface Listener {
        void onSingleArtistSelection(String artist);
    }
}
