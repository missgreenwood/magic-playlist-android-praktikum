package controllers.mainFragments.myplaylistsFragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spotify.sdk.android.playback.Config;

import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import tests.R;

public class MediaPlayerFragment extends Fragment implements View.OnClickListener, PlayQueue.Listener {

    private TextView songInfo;

    private Config spotifyConfig;

    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    public Config getSpotifyConfig() {
        return spotifyConfig;
    }

    public void setSpotifyConfig(Config spotifyConfig) {

        Log.d("", "set spotify config");
        this.spotifyConfig = spotifyConfig;
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

        PlayQueue.getInstance().addObserver(this);

        songInfo = (TextView) v.findViewById(R.id.songInfo);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextSongBtn:
                Log.v("", "call play queue next track");
                PlayQueue.getInstance().nextTrack();
                break;

            case R.id.lastSongBtn:

                Log.v("", "call play queue previous track");
                PlayQueue.getInstance().previousTrack();

                break;

            case R.id.pauseSongBtn:
                Log.v("", "pause button");
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
