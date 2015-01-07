package controllers.mainFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import controllers.MainActivity;
import controllers.mainFragments.generatorFragments.ArtistsFragment;
import controllers.mainFragments.generatorFragments.GenresListFragment;
import controllers.mainFragments.generatorFragments.SongsFragment;
import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import models.playlist.PlaylistGenerator;
import tests.R;

/**
 * created by Andreas 06.01.2015
 */
public class GeneratorFragment extends android.support.v4.app.Fragment implements
        PlaylistGenerator.Listener,
        GenresListFragment.OnGenrePass,
        ArtistsFragment.OnArtistPass,
        SongsFragment.OnSongPass,
        View.OnClickListener {


    private PlaylistGenerator generator;
    private GenresListFragment genresListFragment;
    private ArtistsFragment artistsFragment;
    private SongsFragment songsFragment;

    private String artist;
    private String song;
    private String genre;

    private boolean isPlayingSingleSong = false;

    public GeneratorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        generator = new PlaylistGenerator(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generator, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Playlist Generator");
        view.findViewById(R.id.genresBtn).setOnClickListener(this);
        view.findViewById(R.id.artistsBtn).setOnClickListener(this);
        view.findViewById(R.id.songsBtn).setOnClickListener(this);
        view.findViewById(R.id.startGeneratorBtn).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Playlist Generator");
    }

    public void startGeneratorClicked(View view)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final EditText nameInput = new EditText(getActivity().getApplicationContext());
        nameInput.setTextColor(Color.BLACK);
        final GeneratorFragment _this = this;

        dialogBuilder.setTitle("set playlist name");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (artist != null) {
                    generator.createNewPlaylist(nameInput.getText().toString());
                    if (song == null) {
                        Song song = new Song();
                        song.setArtist(artist);
                        generator.getNextSong(song);
                    } else {
                        generator.getNextSong(new Song(_this.artist, _this.song));
                    }
                } else {
                    AlertDialog.Builder setArtistInfo = new AlertDialog.Builder(_this.getActivity());
                    setArtistInfo.setTitle("set first artist info before!");
                    setArtistInfo.setNeutralButton("Okay", null);
                    setArtistInfo.setMessage("You have to insert an artist, which will be used to find similar artists!");
                    setArtistInfo.create().show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setView(nameInput);
        dialogBuilder.create().show();
    }

    public void genresClicked(View view) {

        genresListFragment = (GenresListFragment)getActivity().getSupportFragmentManager().findFragmentByTag("genresListFragment");
        if (genresListFragment==null) {
            genresListFragment = new GenresListFragment();
            genresListFragment.setListener(this);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(android.R.id.content,genresListFragment,"genresListFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void artistsClicked(View view) {
        artistsFragment = (ArtistsFragment)getActivity().getSupportFragmentManager().findFragmentByTag("artistsFragment");
        if (artistsFragment==null) {
            artistsFragment = new ArtistsFragment();
            artistsFragment.setListener(this);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup, artistsFragment, "artistsFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void songsClicked(View view) {
        songsFragment = (SongsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("songsFragment");
        if (songsFragment==null) {
            songsFragment = new SongsFragment();
            songsFragment.setListener(this);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup, songsFragment, "songsFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    @Override
    public void nextSongFound(final Song song) {
        final PlaylistGenerator _generator = generator;
        final GeneratorFragment _this = this;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Song zu Playliste hinzufügen?")
                .setMessage("Song: " + song.getArtist() + " - " + song.getSongname())
                .setCancelable(false);
        dialogBuilder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _generator.addSongToPlaylist(song);
                _generator.getNextSong(song);
                _this.stopPlayingSingleSong(song);
            }
        });
        dialogBuilder.setNeutralButton("Verwerfen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _generator.getNextSong(null);
                _this.stopPlayingSingleSong(song);
            }
        });
        dialogBuilder.setNegativeButton("Abschließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _this.finishPlaylistClicked();
                _this.stopPlayingSingleSong(song);
            }
        });
        
        Button playSongBtn = new Button(getActivity());
        playSongBtn.setText("play song");
        playSongBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_this.isPlayingSingleSong) {
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
                _this.isPlayingSingleSong = !_this.isPlayingSingleSong;
            }
        });
        dialogBuilder.setView(playSongBtn);
        
        dialogBuilder.create().show();
    }

    @Override
    public void nextSongError(int errorStatus) {
        generator.getNextSong(null);
    }

    private void finishPlaylistClicked() {
        generator.savePlaylist();
        Toast.makeText(getActivity().getApplicationContext(), "playlist " + generator.getPlaylist().getName(), Toast.LENGTH_SHORT);
    }

    @Override
    public void onGenrePass(String data) {
        this.genre = data;
        // Display selectedGenre in Log
        Log.d("LOG", "Selected genre: " + data);
    }

    @Override
    public void onArtistPass(String data) {
        this.artist = data;
        // Display enteredArtist in Log
        Log.d("LOG", "Entered artist: " + data);
    }

    @Override
    public void onSongPass(String data) {
        this.song = data;
        // Display enteredSong in Log
        Log.d("LOG", "Entered song: " + data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.genresBtn:
                genresClicked(v);
                break;
            case R.id.artistsBtn:
                artistsClicked(v);
                break;
            case R.id.songsBtn:
                songsClicked(v);
                break;
            case R.id.startGeneratorBtn:
                startGeneratorClicked(v);
                break;
        }
    }

    private boolean isPlayingSingleSong(Song song) {
        Song currentSong = PlayQueue.getInstance().getCurrentSong();
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
