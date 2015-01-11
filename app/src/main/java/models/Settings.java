package models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import models.mediaModels.Playlist;
import models.mediaModels.Song;
import models.mediawrappers.PlayQueue;
import models.playlist.PlaylistsManager;

/**
 * Created by TheDaAndy on 07.01.2015.
 */
public class Settings {
    private static Settings instance = new Settings();
    private Listener listener;
    private ArrayList<String> usedMediaWrappers;
    private ArrayList<String> mediaWrappers;
    private SharedPreferences preferences;

    private Settings() {
    }

    public static Settings getInstance() {
        return instance;
    }

    public ArrayList<String> getMediaWrappers() {
        return getMediaWrappers(false);
    }

    public ArrayList<String> getMediaWrappers(boolean inclusiveInactive)
    {
        Log.d("SETTINGS", "usedMediaWrappers: " + usedMediaWrappers);
        if (inclusiveInactive) {
            return mediaWrappers;
        } else {
            return usedMediaWrappers;
        }
    }

    public void setOnMediaWrapperListChangeListener(Listener listener) {
        this.listener = listener;
    }

    public void loadSettings(SharedPreferences preferences)
    {
        ArrayList<String> defWrapperList = getDefaultMediaWrappersList();
        usedMediaWrappers = new ArrayList<>();

        //you can't allocate a ArrayList size, so we have to use a normal Array first
        String[] wrappers = new String[defWrapperList.size()];
        for (int i = 0; i < defWrapperList.size(); i++) {
            String wrapper = defWrapperList.get(i);
            //add to index, which represents the priority of the wrapper
            wrappers[preferences.getInt(wrapper, i)] = wrapper;
            if (preferences.getBoolean(wrapper + "_bool", true)) {
                usedMediaWrappers.add(wrapper);
            }
        }
        //now we can init the ArrayList with the right order
        mediaWrappers = new ArrayList<>();
        for (String wrapper : wrappers) {
            mediaWrappers.add(wrapper);
        }

        this.preferences = preferences;
    }

    private ArrayList<String> getDefaultMediaWrappersList()
    {
        ArrayList<String> defaultWrappersList = new ArrayList<>();
        defaultWrappersList.add(Song.MEDIA_WRAPPER_LOCAL_FILE);
        defaultWrappersList.add(Song.MEDIA_WRAPPER_REMOTE_SOUNDCLOUD);
        defaultWrappersList.add(Song.MEDIA_WRAPPER_SPOTIFY);
        return defaultWrappersList;
    }

    private void saveSettings()
    {
        if (preferences == null) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < mediaWrappers.size(); i++) {
            String wrapper = mediaWrappers.get(i);
            editor.putInt(wrapper, i);
            editor.putBoolean(wrapper + "_bool", isWrapperActive(wrapper));
        }
        editor.apply();
        listener.onMediaWrapperListChange(getMediaWrappers());
    }

    public void increaseWrapperPriority(String wrapper) {
        int prio = mediaWrappers.indexOf(wrapper);
        if (prio > 0) { //its not already the first item
            String upperWrapper = mediaWrappers.get(prio-1);
            mediaWrappers.set(prio-1, mediaWrappers.get(prio));
            mediaWrappers.set(prio, upperWrapper);
        }
        saveSettings();
        resetPlaylists();
    }

    public void decreaseWrapperPriority(String wrapper) {
        int prio = mediaWrappers.indexOf(wrapper);
        if (prio < mediaWrappers.size() - 1) { //its not already the last item
            String upperWrapper = mediaWrappers.get(prio + 1);
            mediaWrappers.set(prio + 1, mediaWrappers.get(prio));
            mediaWrappers.set(prio, upperWrapper);
        }
        saveSettings();
        resetPlaylists();
    }

    public void deactivateWrapper(String wrapper) {
        usedMediaWrappers.remove(wrapper);
        saveSettings();
        resetPlaylists();
    }

    public void activateWrapper(String wrapper) {
        if (!usedMediaWrappers.contains(wrapper)) {
            usedMediaWrappers.add(wrapper);
        }
        saveSettings();
        resetPlaylists();
    }

    private void resetPlaylists()
    {
        for (Playlist playlist : PlaylistsManager.getInstance().getPlaylists()) {


            for (Song song : playlist.getSongsList()) {
                // song.setMediaWrapper(null);
                song.setNotPlayable(false);
            }


        }

        // PlayQueue.getInstance().pausePlayer();
        PlayQueue.getInstance().initializePlaylist(true);

        Log.d("", "current song in resetPlaylists: " + PlayQueue.getInstance().getCurrentSong());


    }

    public boolean isWrapperActive(String wrapper) {
        return usedMediaWrappers.contains(wrapper);
    }

    public interface Listener {
        void onMediaWrapperListChange(ArrayList<String> mediaWrappers);
    }
}
