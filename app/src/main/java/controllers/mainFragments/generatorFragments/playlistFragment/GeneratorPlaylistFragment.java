package controllers.mainFragments.generatorFragments.playlistFragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import controllers.MainActivity;
import controllers.mainFragments.generatorFragments.PlaylistFragment;
import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import models.playlist.PlaylistGenerator;

/**
 * created by Andreas 08.01.2015
 */
public class GeneratorPlaylistFragment extends PlaylistFragment implements PlaylistGenerator.Listener{

    private static final String TAG = "main.java.controllers.mainFragments.generatorFragments.playlistFragment.GeneratorPlaylistFragment";
    private PlaylistGenerator generator = new PlaylistGenerator(this);
    private boolean isPlayingSingleSong = false;
    private boolean waitForGeneratorCallback = false;
    private ProgressDialog loadingDialog;

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
        generator.setGenre(args.getString("genre"));
        generator.setInitSong(new Song(args.getString("artist"), args.getString("songname")));
        setPlaylist(generator.createNewPlaylist(args.getString("playlistName")));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Song nextSong = generator.getNextSong(true);
        if (nextSong != null) {
            showSongSuggestion(nextSong);
        } else if (generator.getRequestsCount() > 0) {
            setLoading(true);
            waitForGeneratorCallback = true;
        } else {
            Log.e(TAG, "no song found");
        }
    }

    @Override
    public void onDestroy() {
        generator = null;
        loadingDialog = null;
        super.onDestroy();
    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = ProgressDialog.show(getActivity(), "Loading", "Initializing generator data");
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private void showSongSuggestion(final Song song) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Song zu Playliste hinzufügen?")
                .setMessage("Song: " + song.getArtist() + " - " + song.getSongname())
                .setCancelable(false);
        dialogBuilder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                generator.addSongToPlaylist(song);
                stopPlayingSingleSong(song);
                showSongSuggestion(generator.getNextSong(true));
            }
        });
        dialogBuilder.setNeutralButton("Verwerfen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopPlayingSingleSong(song);
                showSongSuggestion(generator.getNextSong(false));
            }
        });
        dialogBuilder.setNegativeButton("Abschließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopPlayingSingleSong(song);
                finishPlaylistClicked();
            }
        });

        Button playSongBtn = new Button(getActivity());
        playSongBtn.setText("play song");
        playSongBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPlayingSingleSong) {
                    ((Button)v).setText("play song");
                    PlayQueue.getInstance().pausePlayer();
                } else {
                    ((Button)v).setText("stop song");
                    if (PlayQueue.getInstance().getCurrentSong() == song) {
                        PlayQueue.getInstance().resumePlayer();
                    } else {
                        PlayQueue.getInstance().playSingleSong(song);
                    }
                }
                isPlayingSingleSong = !isPlayingSingleSong;
            }
        });
        dialogBuilder.setView(playSongBtn);

        dialogBuilder.create().show();
    }

    @Override
    public void callbacksFinished() {
        if (waitForGeneratorCallback) {
            waitForGeneratorCallback = false;
            setLoading(false);

            Song nextSong = generator.getNextSong(true);
            if (nextSong != null) {
                showSongSuggestion(nextSong);
            } else {
                Log.e(TAG, "no song found");
            }
        }
    }

    @Override
    public void callbackError(int errorStatus) {
        generator.getNextSong(false);
    }

    private void finishPlaylistClicked() {
        generator.savePlaylist();
        Toast.makeText(getActivity(), "playlist " + generator.getPlaylist().getName(), Toast.LENGTH_SHORT).show();
    }

    private boolean isPlayingSingleSong(Song song) {
        Song currentSong = PlayQueue.getInstance().getCurrentSong();
        Log.w(TAG, "song == currentSong \"" + String.valueOf(song == currentSong) + "\" && PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING \"" +  String.valueOf(PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING) + "\": " + String.valueOf(song == currentSong && PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING));
        return song == currentSong &&
                PlayQueue.getInstance().getState() == PlayQueue.STATE_ALREADY_PlAYING;
    }

    private void stopPlayingSingleSong(Song song) {
        if (isPlayingSingleSong(song)) {
            song.getMediaWrapper().stopPlayer();
            PlayQueue.getInstance().setCurrentSong(null);
        } else if (PlayQueue.getInstance().getCurrentSong() == song) {
            PlayQueue.getInstance().setCurrentSong(null);
        }
        isPlayingSingleSong = false;
    }
}
