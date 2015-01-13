package controllers.mainFragments.browserFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import controllers.mainFragments.generatorFragments.PlaylistFragment;
import models.mediaModels.Playlist;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import tests.R;

/**
 * Created by judith on 12.01.15.
 */
public class ResultPlaylistFragment extends PlaylistFragment {
    private Button starButton;
    private Button saveButton;
    private int id;
    private String playlist_name;
    private Playlist playlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result_playlist, container, false);
        starButton = (Button) v.findViewById(R.id.starBtn);
        saveButton = (Button) v.findViewById(R.id.saveBtn);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You just liked this playlist!", Toast.LENGTH_LONG).show();
                Client.getInstance().likePlaylist(playlist);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You just saved this playlist!", Toast.LENGTH_LONG).show();
                PlaylistsManager.getInstance().addPlaylist(playlist);
            }
        });
        return v;
    }

    public void setLikeData(Bundle args) {
        Log.d("", "Liked playlist object: " + args.getParcelable("playlist"));
        this.playlist = args.getParcelable("playlist");
        if (playlist instanceof Playlist) {
            Log.d("", "Playlist has correct datatype!");
        }
        else {
            Log.d("", "Playlist datatype is incorrect!");
        }
        this.playlist_name = args.getString("playlist_name");
    }
}