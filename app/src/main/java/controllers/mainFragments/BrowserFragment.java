package controllers.mainFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import controllers.MainActivity;
import controllers.mainFragments.browserFragments.playlistFragment.BrowserPlaylistFragment;
import controllers.mainFragments.generatorFragments.ArtistsFragment;
import controllers.mainFragments.generatorFragments.GenresListFragment;
import controllers.mainFragments.generatorFragments.SingleArtistFragment;
import tests.R;

/**
 * Created by judith on 02.01.15.
 */
public class BrowserFragment extends android.support.v4.app.Fragment implements
        GenresListFragment.OnGenrePass,
        SingleArtistFragment.Listener,
        View.OnClickListener {
    private GenresListFragment genresFragment;
    private SingleArtistFragment artistsFragment;
    private BrowserPlaylistFragment browserPlaylistFragment;
    private String genre;
    private String artist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Playlist Browser");
        view.findViewById(R.id.searchGenre).setOnClickListener(this);
        view.findViewById(R.id.searchArtist).setOnClickListener(this);
        view.findViewById(R.id.startSearch).setOnClickListener(this);
        return view;
    }

    private void createSearchView() {
        Bundle args = new Bundle();
        args.putString("artist", artist);
        args.putString("genre", genre);
        browserPlaylistFragment = (BrowserPlaylistFragment)getActivity().getSupportFragmentManager().findFragmentByTag("browserPlaylistFragment");
        if (browserPlaylistFragment==null) {
            browserPlaylistFragment = new BrowserPlaylistFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.browserMainViewGroup,browserPlaylistFragment,"browserPlaylistFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
        browserPlaylistFragment.setSearchData(args);
    }

    @Override
    public void onGenrePass(String data) {
        this.genre = data;
    }

    public void searchGenreClicked(View view) {

        genresFragment = (GenresListFragment)getActivity().getSupportFragmentManager().findFragmentByTag("genresSearchFragment");
        if (genresFragment==null) {
            genresFragment = new GenresListFragment();
            genresFragment.setListener(this);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.browserMainViewGroup,genresFragment,"genresSearchFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void searchArtistClicked(View view) {
        artistsFragment = (SingleArtistFragment)getActivity().getSupportFragmentManager().findFragmentByTag("artistSearchFragment");
        if (artistsFragment == null) {
            artistsFragment = new SingleArtistFragment();
            artistsFragment.setListener(this);
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.add(R.id.browserMainViewGroup,artistsFragment, "artistSearchFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void startSearchClicked(View view)
    {
        if ((artist == null || artist.isEmpty()) && (genre == null || genre.isEmpty()) ) {
            AlertDialog.Builder setArtistInfo = new AlertDialog.Builder(getActivity());
            setArtistInfo.setTitle("No initial info given!");
            setArtistInfo.setNeutralButton("Okay", null);
            setArtistInfo.setMessage("You have to set some initial info like genre or artist before!");
            setArtistInfo.create().show();
            return;
        }
        createSearchView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchGenre:
                searchGenreClicked(v);
                break;
            case R.id.searchArtist:
                searchArtistClicked(v);
                break;
            case R.id.startSearch:
                startSearchClicked(v);
                break;
        }
    }

    @Override
    public void onSingleArtistSelection(String artist) {
        this.artist = artist;
    }
}