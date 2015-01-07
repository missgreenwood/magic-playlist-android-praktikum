package models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 07.01.2015.
 */
public class Settings {
    private static Settings instance = new Settings();

    public static Settings getInstance() {
        return instance;
    }

    private ArrayList<String> mediaWrappers;
    private SharedPreferences preferences;

    private Settings() {
    }

    public ArrayList<String> getMediaWrappers()
    {
        return mediaWrappers;
    }

    public void loadSettings(SharedPreferences preferences)
    {
        ArrayList<String> defWrapperList = getDefaultMediaWrappersList();
        mediaWrappers = new ArrayList<>(defWrapperList.size());
        for (int i = 0; i < defWrapperList.size(); i++) {
            String wrapper = defWrapperList.get(i);
            //add to index, which represents the priority of the wrapper
            mediaWrappers.add(preferences.getInt(wrapper, i), wrapper);
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
            editor.putInt(mediaWrappers.get(i), i);
        }

        editor.apply();
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
}
