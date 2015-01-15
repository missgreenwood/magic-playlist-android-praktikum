package controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import controllers.mainFragments.BrowserFragment;
import controllers.mainFragments.GeneratorFragment;
import controllers.mainFragments.MyPlaylistsFragment;
import controllers.mainFragments.SettingsFragment;
import models.mediawrappers.SpotifyLoginHandler;
import tests.R;

public class MainFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main_fragment, container, false);

        v.findViewById(R.id.myPlaylistsBtn).setOnClickListener(this);
        v.findViewById(R.id.playlistsGeneratorBtn).setOnClickListener(this);
        v.findViewById(R.id.settingsBtn).setOnClickListener(this);
        v.findViewById(R.id.otherPlaylistsBtn).setOnClickListener(this);
        v.findViewById(R.id.button5).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myPlaylistsBtn:
                this.openMyPlaylists();
                break;
            case R.id.playlistsGeneratorBtn:
                openPlaylistGenerator();
                break;
            case R.id.settingsBtn:
                this.openSettings();
                break;
            case R.id.otherPlaylistsBtn:
                this.openPlaylistBrowser();
                break;
            case R.id.button5:
                SpotifyLoginHandler spotifyLoginHandler = SpotifyLoginHandler.getInstance();
                spotifyLoginHandler.setContext(getActivity());
                spotifyLoginHandler.openAuthWindow();
                break;
        }
    }

    public void openMyPlaylists() {
        MyPlaylistsFragment playlistsListFragment = (MyPlaylistsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("playlistsListFragment");
        if (playlistsListFragment == null) {
            playlistsListFragment = new MyPlaylistsFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, playlistsListFragment, "playlistsListFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void openPlaylistGenerator() {
        GeneratorFragment generatorFragment = (GeneratorFragment) getActivity().getSupportFragmentManager().findFragmentByTag("generatorFragment");
        if (generatorFragment == null) {
            generatorFragment = new GeneratorFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, generatorFragment, "generatorFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void openSettings() {
        SettingsFragment settingsFragment = (SettingsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("settingsFragment");
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, settingsFragment, "settingsFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }

    public void openPlaylistBrowser() {
        BrowserFragment browserFragment = (BrowserFragment) getActivity().getSupportFragmentManager().findFragmentByTag("browserFragment");
        if (browserFragment == null) {
            browserFragment = new BrowserFragment();
            FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
            transact.replace(R.id.mainViewGroup, browserFragment, "browserFragment");
            transact.addToBackStack(null);
            transact.commit();
        }
    }
}
