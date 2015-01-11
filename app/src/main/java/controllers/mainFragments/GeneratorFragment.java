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
import android.widget.NumberPicker;

import controllers.MainActivity;
import controllers.mainFragments.generatorFragments.ArtistsFragment;
import controllers.mainFragments.generatorFragments.GenresListFragment;
import controllers.mainFragments.generatorFragments.SongsFragment;
import controllers.mainFragments.generatorFragments.playlistFragment.GeneratorPlaylistFragment;
import models.playlist.PlaylistsManager;
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
    private int songsCountLimit = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generator, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Playlist Generator");
        view.findViewById(R.id.songsCountLimitBtn).setOnClickListener(this);
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

        showSetPlaylistNameDialog(false);
    }

    private void showSetPlaylistNameDialog(boolean alreadyExists)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final EditText nameInput = new EditText(getActivity().getApplicationContext());
        nameInput.setSingleLine();
        nameInput.setTextColor(Color.BLACK);

        dialogBuilder.setTitle("Playlist name:");

        if (alreadyExists) {
            dialogBuilder.setMessage("Playlist name already exists, please choose another one.");
        }

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistName = nameInput.getText().toString().trim();
                if (playlistName != null && !playlistName.isEmpty()) {
                    if (PlaylistsManager.getInstance().isPlaylistNameUnique(playlistName)) {
                        createGeneratorView(playlistName);
                    } else {
                        showSetPlaylistNameDialog(true);
                    }
                } else {
                    showSetPlaylistNameDialog(false);
                }
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
        args.putInt("songsCountLimit", songsCountLimit);

        playlistFragment = (GeneratorPlaylistFragment)getActivity().getSupportFragmentManager().findFragmentByTag("generatorPlaylistFragment");
        if (playlistFragment==null) {
            playlistFragment = new GeneratorPlaylistFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.generatorMainViewGroup,playlistFragment,"generatorPlaylistFragment");
//            transact.addToBackStack(null); //not needed, because generation is completed now
            transact.commit();
        }
        playlistFragment.setGeneratorData(args);

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

    private void songsCountClicked(View v) {
        AlertDialog.Builder countDialog = new AlertDialog.Builder(getActivity());
        countDialog.setTitle("How many songs shall be generated?");

        final NumberPicker nmbrPickr = new NumberPicker(countDialog.getContext());
        nmbrPickr.setMinValue(0);
        nmbrPickr.setMaxValue(9999);
        nmbrPickr.setValue(songsCountLimit);

        countDialog.setView(nmbrPickr);
        countDialog.setPositiveButton("okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                songsCountLimit = nmbrPickr.getValue();
            }
        });
        countDialog.setNegativeButton("cancel", null);
        countDialog.create().show();
    }

    @Override
    public void onGenrePass(String data) {
        this.genre = data;
    }

    @Override
    public void onArtistPass(String data) {
        this.artist = data;
    }

    @Override
    public void onSongPass(String data) {
        this.songname = data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.songsCountLimitBtn:
                songsCountClicked(v);
                break;
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
