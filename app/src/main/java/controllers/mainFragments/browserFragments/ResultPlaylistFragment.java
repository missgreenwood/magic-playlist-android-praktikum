package controllers.mainFragments.browserFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;

import controllers.mainFragments.generatorFragments.PlaylistFragment;
import models.mediaModels.Playlist;
import models.mediawrappers.PlayQueue;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ResultPlaylistFragment _this = this;
        View v = inflater.inflate(R.layout.fragment_result_playlist, container, false);

        v.findViewById(R.id.starBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                requestHandle = Client.getInstance().likePlaylist(_this, playlist);
            }
        });
        v.findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistsManager.getInstance().addPlaylist(playlist);
                if (playlist.save()) {
                    Toast.makeText(getActivity(), "You just saved this playlist!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Error while saving playlist!", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //important to override, because otherwise, you can delete songs from playlist!
    }

    @Override
    public void onLikePlaylistSuccess() {
        setLoading(false);
        Toast.makeText(getActivity(), "Playlist successfully liked!", Toast.LENGTH_SHORT).show();
        playlist.setLikes(playlist.getLikes() + 1);
    }

    @Override
    public void onLikePlaylistError() {
        setLoading(false);
        Toast.makeText(getActivity(), "Error while sending playlist like!", Toast.LENGTH_SHORT).show();
    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = new ProgressDialog(getActivity());
            loadingDialog.setMessage("Uploading playlist \"" + playlist.getName() + "\"...");
            loadingDialog.setTitle("Upload playlist");
            loadingDialog.setCancelable(true);
            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (requestHandle != null && !requestHandle.isFinished()) {
                        requestHandle.cancel(true);
                        Toast.makeText(getActivity(), "Uploading request canceled!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            loadingDialog.show();
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}