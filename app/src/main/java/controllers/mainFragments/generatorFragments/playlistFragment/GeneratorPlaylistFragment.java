package controllers.mainFragments.generatorFragments.playlistFragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import controllers.mainFragments.generatorFragments.PlaylistFragment;
import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.playlist.PlaylistGenerator;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import rest.client.ClientListener;

/**
 * created by Andreas 08.01.2015
 */
public class GeneratorPlaylistFragment extends PlaylistFragment implements
        PlaylistGenerator.Listener
{

    private static final String TAG = "main.java.controllers.mainFragments.generatorFragments.playlistFragment.GeneratorPlaylistFragment";
    private PlaylistGenerator generator;
    private ProgressDialog loadingDialog;
    private boolean uploading = false;
    private boolean destroying = false;


    public void setGeneratorData(String genre, int songsCountLimit, ArrayList<String> artists, String playlistName) {
        generator = new PlaylistGenerator(this, genre, songsCountLimit, artists);
        setPlaylist(generator.createNewPlaylist(playlistName));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoading(true);
        generator.generatePlaylist();
    }

    @Override
    public void onDestroy() {
        destroying = true;
        generator.finishGeneration();
        generator = null;
        loadingDialog = null;
        super.onDestroy();
    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = new ProgressDialog(getActivity());
            loadingDialog.setMessage("Generating playlist");
            loadingDialog.setTitle("Loading");
            loadingDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    generator.finishGeneration();
                }
            });
            loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    generator.finishGeneration();
                }
            });
            loadingDialog.show();
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void callbackError(int errorStatus) {
        Log.e(TAG, "error while generation, statuscode: " + errorStatus);
    }

    @Override
    public void generationFinished(final Playlist playlist) {

        if (!PlaylistsManager.getInstance().addPlaylist(playlist)) {
            Log.e("GENERATOR", "error while adding generated playlist to manager");
        }
        Toast.makeText(getActivity().getApplicationContext(), "Playlist \"" + playlist.getName() + "\" successfully created!", Toast.LENGTH_SHORT).show();

        if (destroying) {
            return;
        }

        setLoading(false);
        final GeneratorPlaylistFragment _this = this;

        AlertDialog.Builder uploadDialog = new AlertDialog.Builder(getActivity());
        uploadDialog.setTitle("Upload playlist?");
        uploadDialog.setMessage("Do you want to upload your playlist? Other can see them in the playlist browser. They will only see your username.");
        uploadDialog.setPositiveButton("yes, upload Playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingDialog = ProgressDialog.show(getActivity(),
                        "Uploading playlist \"" + generator.getPlaylist().getName() + "\"",
                        "uploading...");
                uploading = true;
                Client.getInstance().addPlaylist(_this, playlist);
            }
        });
        uploadDialog.setNegativeButton("no", null);
        uploadDialog.setCancelable(true);
        uploadDialog.create().show();
    }

    @Override
    public void onAddPlaylistSuccess() {
        if (uploading) {
            uploading = false;
            Toast.makeText(getActivity().getApplicationContext(), "Playlist \"" + generator.getPlaylist().getName() + "\" successfully uploaded", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onAddPlaylistError(boolean alreadyExists) {
        if (uploading) {
            uploading = false;
            String alreadyExistsString = alreadyExists ? " Playlist name already exists." : "Error occured while trying to upload playlist!";
            Toast.makeText(getActivity().getApplicationContext(), alreadyExistsString, Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    public void setInitSongs(ArrayList<Song> initSongs) {
        generator.setInitSongs(initSongs);
    }
}
