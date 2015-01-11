package models.metadatawrappers;

import java.util.ArrayList;

import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 28.12.2014.
 */
public interface LastFmListener {
    /**
     * will be called when the similar artists api call finishes
     * @param calledArtist artist, that was given in requestCall
     * @param returnedArtist artists, which similarArtists are given
     * @param similarArtists array [[String id, String name, String (parseable to double)fitting], ...]
     * */
    void onSimilarArtistsCallback(String calledArtist, String returnedArtist, String[][] similarArtists);
    void onTopTracksCallback(String calledArtist, String returnedArtist, ArrayList<String> trackNames);
    void onGenreArtistsCallback(String[][] artistsArray);
}
