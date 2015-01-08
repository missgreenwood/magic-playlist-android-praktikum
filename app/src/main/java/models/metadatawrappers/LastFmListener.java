package models.metadatawrappers;

import java.util.ArrayList;

import models.mediaModels.Song;

/**
 * Created by TheDaAndy on 28.12.2014.
 */
public interface LastFmListener {
    /**
     * will be called when the similar artists api call finishes
     * @param artists array [[String id, String name, String (parseable to double)fitting], ...]
     * */
    public void onSimilarArtistsCallback(String[][] artists);
    public void onTopTracksCallback(String artistName, ArrayList<Song> tracks);

    void onTagArtistsCallback(String[][] artistsArray);
}
