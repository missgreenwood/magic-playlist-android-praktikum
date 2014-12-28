package playlistGenerator;

import com.example.mymodule.mymodule.app.Song;

import java.util.ArrayList;

/**
 * Created by TheDaAndy on 27.12.2014.
 */
public class Playlist {

    private ArrayList<Song> songs;

    public Playlist () {
        songs = new ArrayList<>();
    }

    public void addSong(Song newSong) {
        songs.add(newSong);
    }

    public void removeSong (Song song) {
        songs.remove(song);
    }

    public ArrayList<Song> getSongsList() {
        return songs;
    }
}
