package controllers.mainFragments.generatorFragments.playlistFragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import controllers.MainActivity;
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
        PlaylistGenerator.Listener,
        ClientListener.AddPlaylistListener
{

    private static final String TAG = "main.java.controllers.mainFragments.generatorFragments.playlistFragment.GeneratorPlaylistFragment";
    private PlaylistGenerator generator;
    private ProgressDialog loadingDialog;
    private boolean uploading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(generator.getPlaylist().getName());
    }

    public void setGeneratorData(Bundle args) {
        generator = new PlaylistGenerator(this, args.getString("genre"), args.getInt("songsCountLimit"), new Song(args.getString("artist"), args.getString("songname")));
        setPlaylist(generator.createNewPlaylist(args.getString("playlistName")));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoading(true);
        generator.setContext(getActivity().getApplicationContext());
        generator.startGeneration();
    }

    @Override
    public void onDestroy() {
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
        final GeneratorPlaylistFragment _this = this;

        setLoading(false);
        PlaylistsManager.getInstance().addPlaylist(playlist);
        Toast.makeText(getActivity().getApplicationContext(), "Playlist \"" + playlist.getName() + "\" successfully created!", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder uploadDialog = new AlertDialog.Builder(getActivity());
        uploadDialog.setTitle("Upload playlist?");
        uploadDialog.setMessage("Do you want to upload your playlist? Other can see them in the playlist browser. They will only see your username.");
        uploadDialog.setPositiveButton("yes, upload Playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Client.getInstance().addObserver(_this);
                Client.getInstance().addPlaylist(playlist);
                loadingDialog = ProgressDialog.show(getActivity(),
                        "Uploading playlist \"" + generator.getPlaylist().getName() + "\"",
                        "uploading...");
                uploading = true;
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
            Client.getInstance().removeObserver(this);
            Toast.makeText(getActivity().getApplicationContext(), "Playlist \"" + generator.getPlaylist().getName() + "\" successfully uploaded", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onAddPlaylistError() {
        if (uploading) {
            uploading = false;
            Client.getInstance().removeObserver(this);
            Toast.makeText(getActivity().getApplicationContext(), "Error while uploading playlist \"" + generator.getPlaylist().getName() + "\"!", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

//    private void showSongSuggestion(final Song song) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//        dialogBuilder.setTitle("Song zu Playliste hinzufügen?")
//                .setMessage("Song: " + song.getArtist() + " - " + song.getSongname())
//                .setCancelable(false);
//        dialogBuilder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                generator.addSongToPlaylist(song);
//                stopPlayingSingleSong(song);
//                showSongSuggestion(generator.getNextSong(true));
//            }
//        });
//        dialogBuilder.setNeutralButton("Verwerfen", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                stopPlayingSingleSong(song);
//                showSongSuggestion(generator.getNextSong(false));
//            }
//        });
//        dialogBuilder.setNegativeButton("Abschließen", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                stopPlayingSingleSong(song);
//                finishPlaylistClicked();
//            }
//        });
//
//        Button playSongBtn = new Button(getActivity());
//        playSongBtn.setText("play song");
//        playSongBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (isPlayingSingleSong) {
//                    ((Button)v).setText("play song");
//                    PlayQueue.getInstance().pausePlayer();
//                } else {
//                    ((Button)v).setText("stop song");
//                    if (PlayQueue.getInstance().getCurrentSong() == song) {
//                        PlayQueue.getInstance().resumePlayer();
//                    } else {
//                        PlayQueue.getInstance().playSingleSong(song);
//                    }
//                }
//                isPlayingSingleSong = !isPlayingSingleSong;
//            }
//        });
//        dialogBuilder.setView(playSongBtn);
//
//        dialogBuilder.create().show();
//    }

//    private void finishPlaylistClicked() {
//        generator.savePlaylist();
//        Toast.makeText(getActivity(), "playlist " + generator.getPlaylist().getName(), Toast.LENGTH_SHORT).show();
//    }

//    private boolean isPlayingSingleSong(Song song) {
//        Song currentSong = PlayQueue.getInstance().getCurrentSong();
//        Log.w(TAG, "song == currentSong \"" + String.valueOf(song == currentSong) + "\" && PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING \"" +  String.valueOf(PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING) + "\": " + String.valueOf(song == currentSong && PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING));
//        return song == currentSong &&
//                PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING;
//    }

//    private void stopPlayingSingleSong(Song song) {
//        if (isPlayingSingleSong(song)) {
//            song.getMediaWrapper().stopPlayer();
//            PlayQueue.getInstance().setCurrentSong(null);
//        } else if (PlayQueue.getInstance().getCurrentSong() == song) {
//            PlayQueue.getInstance().setCurrentSong(null);
//        }
//        isPlayingSingleSong = false;
//    }
}
