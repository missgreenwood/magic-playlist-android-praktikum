package de.lmu.playlist.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

    public static final String NAME = "name";

    public static final String GENRE = "genre";

    private String name;

    private Iterable<Song> songs;

    private String genre;

    public Playlist() {
        // dummy constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterable<Song> getSongs() {
        return songs;
    }

    public void setSongs(Iterable<Song> songs) {
        this.songs = songs;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
