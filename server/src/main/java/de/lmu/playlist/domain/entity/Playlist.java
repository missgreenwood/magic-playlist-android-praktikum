package de.lmu.playlist.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

    public static final String NAME = "name";

    public static final String GENRE = "genre";

    private String name;

    private int likes;

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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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
