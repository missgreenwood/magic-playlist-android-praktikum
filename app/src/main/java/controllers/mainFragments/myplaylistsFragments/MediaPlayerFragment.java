package controllers.mainFragments.myplaylistsFragments;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spotify.sdk.android.playback.Config;

import java.util.ArrayList;

import controllers.MyBroadcastReceiver;
import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import tests.R;

public class MediaPlayerFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private PlayQueue playQueue;
    private Config spotifyConfig;

    public PlayQueue getPlayQueue() {
        return playQueue;
    }

    public void setPlayQueue(PlayQueue playQueue) {
        this.playQueue = playQueue;
    }

    public Config getSpotifyConfig() {
        return spotifyConfig;
    }

    public void setSpotifyConfig(Config spotifyConfig) {

        Log.d("", "set spotify config");
        this.spotifyConfig = spotifyConfig;
    }

    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment


        //  SpotifyAuthentication.openAuthWindow(SpotifyMediaWrapper.CLIENT_ID, "token", SpotifyMediaWrapper.REDIRECT_URI,
        //     new String[]{"user-read-private", "streaming"}, null, this);


//        LastfmMetadataWrapper metadataWrapper = new LastfmMetadataWrapper();
//        metadataWrapper.findSimilarArtists("Radiohead", 5);

        // SoundCloudStreamingMediaWrapper sw = new SoundCloudStreamingMediaWrapper(this, new Song("some artistist","Paranoid Android"));

        //  sw.lookForSong();


        View v = inflater.inflate(R.layout.fragment_media_player, container, false);

        v.findViewById(R.id.nextSongBtn).setOnClickListener(this);
        v.findViewById(R.id.lastSongBtn).setOnClickListener(this);
        v.findViewById(R.id.pauseSongBtn).setOnClickListener(this);
        v.findViewById(R.id.playSongBtn).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        Log.d("", "Klick auf Button");
        if (playQueue != null) {
            switch (view.getId()) {
                case R.id.nextSongBtn:
                    Log.d("", "call play queue next track");
                    playQueue.nextTrack();
                    break;

                case R.id.lastSongBtn:
                    Log.d("", "call play queue before track");
                    playQueue.beforeTrack();
                    break;

                case R.id.pauseSongBtn:
                    Log.d("", "pause button");
                    playQueue.pausePlayer();
                    break;

                case R.id.playSongBtn:
                    playQueue.resumePlayer();
                    break;
            }
        }
    }
}
