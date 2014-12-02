package com.example.mymodule.mymodule.app;

/**
 * Created by lotta on 02.12.14.
 */


public class Song {


    //TODO: anderes Package

    //TODO: ändert das so, wie ihrs braucht, also etwas separate Artist-Klasse. Ich werde nur aus dem Song mittels getArtist() den Artist auslesen
    private String artist;
    private String songname;


//TODO: weitere Metadaten


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }


}
