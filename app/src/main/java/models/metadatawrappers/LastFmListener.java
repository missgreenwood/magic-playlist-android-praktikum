package models.metadatawrappers;

import java.util.ArrayList;

import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 28.12.2014.
 */
public interface LastFmListener {
    /**
     * will be called when the similar artists api call finishes
     * @param artist artists, which similarArtists are given
     * @param similarArtists array [[String id, String name, String (parseable to double)fitting], ...]
     * */
    public void onSimilarArtistsCallback(String artist, String[][] similarArtists);
    public void onTopTracksCallback(String artistName, ArrayList<String> trackNames);

    void onGenreArtistsCallback(String[][] artistsArray);
}
