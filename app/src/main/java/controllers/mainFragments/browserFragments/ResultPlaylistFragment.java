package controllers.mainFragments.browserFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import controllers.mainFragments.generatorFragments.PlaylistFragment;
import tests.R;

/**
 * Created by judith on 12.01.15.
 */
public class ResultPlaylistFragment extends PlaylistFragment {
    private Button starButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        starButton = (Button) container.findViewById(R.id.starBtn);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You just liked this playlist!", Toast.LENGTH_LONG).show();
            }
        });
        return inflater.inflate(R.layout.fragment_result_playlist, container, false);
    }
}
// TODO: access rating button correctly
// TODO: implement functionality for rating button