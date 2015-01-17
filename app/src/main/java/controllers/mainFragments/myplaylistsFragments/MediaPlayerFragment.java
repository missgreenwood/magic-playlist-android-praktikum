package controllers.mainFragments.myplaylistsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spotify.sdk.android.playback.Config;

import models.mediaModels.Song;
import models.mediaModels.PlayQueue;
import tests.R;

public class MediaPlayerFragment extends Fragment implements View.OnClickListener, PlayQueue.Listener {

    private TextView songInfo;

    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_media_player, container, false);

        v.findViewById(R.id.nextSongBtn).setOnClickListener(this);
        v.findViewById(R.id.lastSongBtn).setOnClickListener(this);
        v.findViewById(R.id.pauseSongBtn).setOnClickListener(this);
        v.findViewById(R.id.playSongBtn).setOnClickListener(this);

        PlayQueue.getInstance().addObserver(this);

        songInfo = (TextView) v.findViewById(R.id.songInfo);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextSongBtn:
                PlayQueue.getInstance().nextTrack();
                break;

            case R.id.lastSongBtn:
                PlayQueue.getInstance().previousTrack();
                break;
            case R.id.pauseSongBtn:
                PlayQueue.getInstance().pausePlayer();
                break;
            case R.id.playSongBtn:
                PlayQueue.getInstance().resumePlayer();
                break;
        }
    }

    @Override
    public void onNewSongPlaying(Song song) {
        songInfo.setText(song.getArtist() + " - " + song.getSongname()+" in "+song.getMediaWrapperType());
    }

    @Override
    public void onCannotInitializeSong(Song song) {
        //ignore
    }
}
