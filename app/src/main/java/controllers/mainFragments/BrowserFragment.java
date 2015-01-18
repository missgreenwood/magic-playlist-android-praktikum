package controllers.mainFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;

import java.util.ArrayList;
import java.util.List;

import controllers.mainFragments.browserFragments.playlistFragment.BrowserPlaylistFragment;
import controllers.mainFragments.generatorFragments.GenresListFragment;
import controllers.mainFragments.generatorFragments.SingleArtistFragment;
import controllers.mainFragments.myplaylistsFragments.PlaylistArrayAdapter;
import models.mediaModels.Playlist;
import models.playlist.PlaylistsManager;
import rest.client.Client;
import rest.client.ClientListener;
import tests.R;

/**
 * Created by judith on 02.01.15.
 */
public class BrowserFragment extends android.support.v4.app.Fragment implements
        GenresListFragment.OnGenrePass,
        SingleArtistFragment.Listener,
        View.OnClickListener,
        ClientListener.FindPlaylistsListener
{
    private GenresListFragment genresFragment;
    private SingleArtistFragment artistsFragment;
    private BrowserPlaylistFragment browserPlaylistFragment;
    private String genre;
    private String artist;
    private ProgressDialog loadingDialog;
    private RequestHandle requestHandle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);
        view.findViewById(R.id.fastSearch).setOnClickListener(this);
        view.findViewById(R.id.searchGenre).setOnClickListener(this);
        view.findViewById(R.id.searchArtist).setOnClickListener(this);
        view.findViewById(R.id.startSearch).setOnClickListener(this);
        return view;
    }

    private void createSearchView(List<Playlist> playlists) {
        browserPlaylistFragment = (BrowserPlaylistFragment)getActivity().getSupportFragmentManager().findFragmentByTag("browserPlaylistFragment");
        if (browserPlaylistFragment==null) {
            browserPlaylistFragment = new BrowserPlaylistFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, browserPlaylistFragment, "browserPlaylistFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
        browserPlaylistFragment.setPlaylists(playlists);
    }

    private void setLoading(boolean visible) {
        if (visible) {
            if (loadingDialog != null) {
                loadingDialog = null;
            }
            loadingDialog = ProgressDialog.show(getActivity(), "Loading", "Loading matching playlists from database", false, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (requestHandle != null && !requestHandle.isFinished()) {
                        requestHandle.cancel(true);
                        Toast.makeText(getActivity(), "Load playlists request canceled!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
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
            transact.replace(R.id.mainViewGroup, genresFragment, "genresSearchFragment");
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
            transact.replace(R.id.mainViewGroup, artistsFragment, "artistSearchFragment");
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
        setLoading(true);
        requestHandle = Client.getInstance().findPlaylistsByGenreAndArtist(genre, artist, this);
    }

    private void fastSearchClicked(View v) {
        final ArrayList<Playlist> ownPlaylists = PlaylistsManager.getInstance().getPlaylists();
        if (ownPlaylists == null || ownPlaylists.size() == 0) {
            AlertDialog.Builder createPlaylists = new AlertDialog.Builder(getActivity());
            createPlaylists.setTitle("You have no playlists yet!");
            createPlaylists.setNeutralButton("Okay", null);
            createPlaylists.setMessage("You need some playlists first. This feature searches for playlists depending on yours.");
            createPlaylists.create().show();
            return;
        } else {
            Log.d("BrowserFragment", "ownPlaylists: " + ownPlaylists.size() + " " + ownPlaylists);
            final BrowserFragment _this = this;
            final AlertDialog.Builder createPlaylists = new AlertDialog.Builder(getActivity());
            createPlaylists.setTitle("Select one of your playlists!");
            createPlaylists.setSingleChoiceItems(new PlaylistArrayAdapter(createPlaylists.getContext(), R.layout.rows, R.id.txtview, ownPlaylists), -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setLoading(true);
                    Playlist p = ownPlaylists.get(which);
                    requestHandle = Client.getInstance().findSimilarPlaylists(p, _this);
                }
            });
            createPlaylists.create().show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fastSearch:
                fastSearchClicked(v);
                break;
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

    @Override
    public void onFindPlaylistsSuccess(List<Playlist> playlists) {
        setLoading(false);
        playlists.removeAll(PlaylistsManager.getInstance().getPlaylists());
        if (playlists != null && playlists.size() > 0) {
            createSearchView(playlists);
        } else {
            Toast.makeText(getActivity(), "No playlists found for given search filters!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFindPlaylistsError() {
        setLoading(false);
        Toast.makeText(getActivity(), "Error while playlists search. Please try again!", Toast.LENGTH_LONG).show();
    }
}