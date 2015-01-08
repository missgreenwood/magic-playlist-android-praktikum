package de.lmu.playlist.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

    public static final String NAME = "name";

    private String name;

    private Iterable<Song> songs;

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
}
