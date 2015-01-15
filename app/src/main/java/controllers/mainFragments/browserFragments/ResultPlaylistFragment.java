package controllers.mainFragments.browserFragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;

import controllers.mainFragments.generatorFragments.PlaylistFragment;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import rest.client.ClientListener;
import tests.R;

/**
 * Created by judith on 12.01.15.
 */
public class ResultPlaylistFragment extends PlaylistFragment implements ClientListener.LikePlaylistListener {

    private ProgressDialog loadingDialog;
    private RequestHandle requestHandle;

    private Button starBtn;
    private Button saveBtn;

    private boolean alreadyLiked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ResultPlaylistFragment _this = this;
        View v = inflater.inflate(R.layout.fragment_result_playlist, container, false);

        starBtn = (Button) v.findViewById(R.id.starBtn);
        saveBtn = (Button) v.findViewById(R.id.saveBtn);

        if (playlist.isAlreadyUploaded()) {
            disableBtn(saveBtn, "Already uploaded!");
        } else if (PlaylistsManager.getInstance().containsPlaylistName(playlist)) {
            disableBtn(saveBtn, "Playlist with name already exists");
        }

        alreadyLiked = playlist.isAlreadyLiked();
        if (alreadyLiked) {
            disableBtn(starBtn, "Likes: " + playlist.getLikes());
        }
        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!alreadyLiked) {
                    alreadyLiked = true;
                    starBtn.setText("Loading likes...");
                    starBtn.setEnabled(false);
                    requestHandle = Client.getInstance().likePlaylist(_this, playlist);
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlaylistsManager.getInstance().addPlaylist(playlist)) {
                    disableBtn(saveBtn, "Saved!");
                } else {
                    Toast.makeText(getActivity(), "Error while saving playlist!", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    protected void initListeners(View view) {
        //important to override, because otherwise, you can delete songs from playlist!
    }

    @Override
    public void onLikePlaylistSuccess() {
        disableBtn(starBtn, "Likes: " + playlist.getLikes());
    }

    @Override
    public void onLikePlaylistError() {
        alreadyLiked = false;
        starBtn.setText("Star this playlist!");
        starBtn.setEnabled(true);
        Toast.makeText(getActivity(), "Error while sending playlist like!", Toast.LENGTH_SHORT).show();
    }


}