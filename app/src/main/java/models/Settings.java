package models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 07.01.2015.
 */
public class Settings {
    private static Settings instance = new Settings();

    public static Settings getInstance() {
        return instance;
    }

    private Listener listener;

    private ArrayList<String> mediaWrappers;
    private HashMap<String, Boolean> mediaWrappersState;
    private SharedPreferences preferences;

    private Settings() {
    }

    public ArrayList<String> getMediaWrappers()
    {
        ArrayList<String> usedMediaWrappers = new ArrayList<>();
        for (int i = 0; i < mediaWrappers.size(); i++) {
            if (mediaWrappersState.get(mediaWrappers.get(i))) {
                usedMediaWrappers.add(mediaWrappers.get(i));
            }
        }
        Log.d("SETTINGS", "usedMediaWrappers: " + usedMediaWrappers);
        return usedMediaWrappers;
    }

    public void addOnMediaWrapperListChangeListener(Listener listener) {
        this.listener = listener;
    }

    public void loadSettings(SharedPreferences preferences)
    {
        ArrayList<String> defWrapperList = getDefaultMediaWrappersList();
        mediaWrappers = new ArrayList<>(defWrapperList.size());
        mediaWrappersState = new HashMap<>(defWrapperList.size());
        for (int i = 0; i < defWrapperList.size(); i++) {
            String wrapper = defWrapperList.get(i);
            //add to index, which represents the priority of the wrapper
            mediaWrappers.add(preferences.getInt(wrapper, i), wrapper);
            mediaWrappersState.put(wrapper, preferences.getBoolean(wrapper + "_bool", true));
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
            editor.putBoolean(wrapper + "_bool", mediaWrappersState.get(wrapper));
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
    }

    public void decreaseWrapperPriority(String wrapper) {
        int prio = mediaWrappers.indexOf(wrapper);
        if (prio < mediaWrappers.size() - 1) { //its not already the last item
            String upperWrapper = mediaWrappers.get(prio + 1);
            mediaWrappers.set(prio + 1, mediaWrappers.get(prio));
            mediaWrappers.set(prio, upperWrapper);
        }
        saveSettings();
    }

    public void deactivateWrapper(String wrapper) {
        mediaWrappersState.put(wrapper, false);
        saveSettings();
    }

    public void activateWrapper(String wrapper) {
        mediaWrappersState.put(wrapper, true);
        saveSettings();
    }

    public interface Listener {
        void onMediaWrapperListChange(ArrayList<String> mediaWrappers);
    }
}
