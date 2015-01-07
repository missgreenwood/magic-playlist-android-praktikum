package controllers.mainFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import controllers.MainActivity;
import controllers.mainFragments.generatorFragments.ArtistsFragment;
import controllers.mainFragments.generatorFragments.GenresListFragment;
import controllers.mainFragments.generatorFragments.SongsFragment;
import models.mediaModels.Song;
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


    private FragmentActivity ownerActivity;
    private PlaylistGenerator generator;
    private GenresListFragment genresListFragment;
    private ArtistsFragment artistsFragment;
    private SongsFragment songsFragment;

    private String artist;
    private String song;
    private String genre;

    public GeneratorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        generator = new PlaylistGenerator(this);
        ownerActivity = getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generator, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Playlist Generator");
        view.findViewById(R.id.genresBtn).setOnClickListener(this);
        view.findViewById(R.id.artistsBtn).setOnClickListener(this);
        view.findViewById(R.id.songsBtn).setOnClickListener(this);
        view.findViewById(R.id.startGeneratorBtn).setOnClickListener(this);

        return view;
    }

    public void startGeneratorClicked(View view)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ownerActivity);
        final EditText nameInput = new EditText(ownerActivity.getApplicationContext());
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
                    AlertDialog.Builder setArtistInfo = new AlertDialog.Builder(_this.ownerActivity);
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

        genresListFragment = (GenresListFragment)ownerActivity.getSupportFragmentManager().findFragmentByTag("genresListFragment");
        if (genresListFragment==null) {
            genresListFragment = new GenresListFragment();
            genresListFragment.setListener(this);
            FragmentTransaction transact = ownerActivity.getSupportFragmentManager().beginTransaction();
            transact.add(android.R.id.content,genresListFragment,"genresListFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void artistsClicked(View view) {
        artistsFragment = (ArtistsFragment)ownerActivity.getSupportFragmentManager().findFragmentByTag("artistsFragment");
        if (artistsFragment==null) {
            artistsFragment = new ArtistsFragment();
            artistsFragment.setListener(this);
            FragmentTransaction transact = ownerActivity.getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup, artistsFragment, "artistsFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void songsClicked(View view) {
        songsFragment = (SongsFragment) ownerActivity.getSupportFragmentManager().findFragmentByTag("songsFragment");
        if (songsFragment==null) {
            songsFragment = new SongsFragment();
            songsFragment.setListener(this);
            FragmentTransaction transact = ownerActivity.getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup, songsFragment, "songsFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    @Override
    public void nextSongFound(final Song song) {
        final PlaylistGenerator _generator = generator;
        final GeneratorFragment _this = this;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ownerActivity);
        dialogBuilder.setTitle("Song zu Playliste hinzufügen?")
                .setMessage("Song: " + song.getArtist() + " - " + song.getSongname())
                .setCancelable(false);
        dialogBuilder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _generator.addSongToPlaylist(song);
                _generator.getNextSong(song);
            }
        });
        dialogBuilder.setNeutralButton("Verwerfen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _generator.getNextSong(null);
            }
        });
        dialogBuilder.setNegativeButton("Abschließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _this.finishPlaylistClicked();
            }
        });
        dialogBuilder.create().show();
    }

    @Override
    public void nextSongError(int errorStatus) {
        Log.e("nextSongError", String.valueOf(errorStatus));
        generator.getNextSong(null);
    }

    private void finishPlaylistClicked() {
        generator.savePlaylist();
        Toast.makeText(ownerActivity.getApplicationContext(), "playlist " + generator.getPlaylist().getName(), Toast.LENGTH_SHORT);
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
}
