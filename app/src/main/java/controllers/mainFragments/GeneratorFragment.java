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
import android.widget.EditText;

import controllers.MainActivity;
import controllers.mainFragments.generatorFragments.ArtistsFragment;
import controllers.mainFragments.generatorFragments.GenresListFragment;
import controllers.mainFragments.generatorFragments.SongsFragment;
import controllers.mainFragments.generatorFragments.playlistFragment.GeneratorPlaylistFragment;
import tests.R;

/**
 * created by Andreas 06.01.2015
 */
public class GeneratorFragment extends android.support.v4.app.Fragment implements
        GenresListFragment.OnGenrePass,
        ArtistsFragment.OnArtistPass,
        SongsFragment.OnSongPass,
        View.OnClickListener {


    private GenresListFragment genresListFragment;
    private ArtistsFragment artistsFragment;
    private SongsFragment songsFragment;
    private GeneratorPlaylistFragment playlistFragment;

    private String artist;
    private String songname;
    private String genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        if ((songname == null || songname.isEmpty()) &&
            (artist == null || artist.isEmpty()) &&
            (genre == null || genre.isEmpty())
        ) {
            AlertDialog.Builder setArtistInfo = new AlertDialog.Builder(getActivity());
            setArtistInfo.setTitle("No initial info given!");
            setArtistInfo.setNeutralButton("Okay", null);
            setArtistInfo.setMessage("You have to set any initial info like genre, artist or songname before");
            setArtistInfo.create().show();
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final EditText nameInput = new EditText(getActivity().getApplicationContext());
        nameInput.setTextColor(Color.BLACK);

        dialogBuilder.setTitle("Playlist name:");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createGeneratorView(nameInput.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setView(nameInput);
        dialogBuilder.create().show();
    }

    private void createGeneratorView(String playlistName) {

        Bundle args = new Bundle();
        args.putString("playlistName", playlistName);
        args.putString("artist", artist);
        args.putString("genre", genre);
        args.putString("songname", songname);

        playlistFragment = (GeneratorPlaylistFragment)getActivity().getSupportFragmentManager().findFragmentByTag("genresListFragment");
        if (playlistFragment==null) {
            playlistFragment = new GeneratorPlaylistFragment();
            playlistFragment.setArguments(args);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup,playlistFragment,"genresListFragment");
//            transact.addToBackStack(null); //not needed, because generation is completed now
            transact.commit();
        } else {
            playlistFragment.setArguments(args);
        }

    }

    public void genresClicked(View view) {

        genresListFragment = (GenresListFragment)getActivity().getSupportFragmentManager().findFragmentByTag("genresListFragment");
        if (genresListFragment==null) {
            genresListFragment = new GenresListFragment();
            genresListFragment.setListener(this);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup,genresListFragment,"genresListFragment");
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
        this.songname = data;
        // Display enteredSong in Log
        Log.d("LOG", "Entered songname: " + data);
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
