package com.example.mymodule.metadatawrappers;

import com.example.mymodule.mymodule.app.Song;

/**
 * Created by TheDaAndy on 28.12.2014.
 */
public interface LastFmListener {
    /**
     * will be called when the similar artists api call finishes
     * @param artists array with jsonObjects... call artists.get(0).get("name") for the first artist's name
     * */
    public void onSimilarArtistsCallback(String[] artists);
    public void onTopTracksCallback(Song[] tracks);
}
