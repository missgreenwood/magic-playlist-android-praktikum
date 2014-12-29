package de.lmu.playlist.domain.entity;

import org.mongojack.Id;

public class Playlist {

    public static final String AUTHOR = "author";

    @Id
    private String id;

    private String author;

    public Playlist() {
        // dummy
    }

    public Playlist(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
