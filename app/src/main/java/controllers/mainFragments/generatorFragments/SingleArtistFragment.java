package controllers.mainFragments.generatorFragments;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ListView;

public class SingleArtistFragment extends ArtistsFragment {

    private Listener listener;

    public SingleArtistFragment() {
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
            getFragmentManager().popBackStack();
        }
    }

    public interface Listener {
        void onSingleArtistSelection(String artist);
    }
}
