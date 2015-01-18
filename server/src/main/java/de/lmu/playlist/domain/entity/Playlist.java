package de.lmu.playlist.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author martin
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist implements Comparable<Playlist> {

    public static final String NAME = "name";

    public static final String GENRE = "genre";

    private String name;

    private int likes;

    private ArrayList<Song> songs;

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

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int compareTo(Playlist that) {
        if (getSongs() == null || that == null || that.getSongs() == null) {
            return 0;
        }
        Set<String> thisArtists = new HashSet<>();
        for (Song song : this.getSongs()) {
            thisArtists.add(song.getArtist());
        }
        Set<String> thatArtists = new HashSet<>();
        for (Song song : that.getSongs()) {
            thatArtists.add(song.getArtist());
        }
        thisArtists.retainAll(thatArtists);
        return thisArtists.size();
    }
}
