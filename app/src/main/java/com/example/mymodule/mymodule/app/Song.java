package com.example.mymodule.mymodule.app;

import com.example.mymodule.mediawrappers.AbstractMediaWrapper;

/**
 * Created by lotta on 02.12.14.
 */


public class Song {


    //TODO: anderes Package

    public static String MEDIA_WRAPPER_LOCAL_FILE = "local file";
    public static String MEDIA_WRAPPER_REMOTE_SOUNDCLOUD = "remote file soundcloud";
    public static String MEDIA_WRAPPER_SPOTIFY = "media warpper spotify";
    //TODO: ändert das so, wie ihrs braucht, also etwas separate Artist-Klasse. Ich werde nur aus dem Song mittels getArtist() den Artist auslesen
    private String artist;
    private String songname;
    private AbstractMediaWrapper mediaWrapper;
    private String wrapperType;
    public Song(String artist, String songname) {
        this.artist = artist;
        this.songname = songname;
    }

    public AbstractMediaWrapper getMediaWrapper() {
        return mediaWrapper;
    }

    public void setMediaWrapper(AbstractMediaWrapper mediaWrapper) {
        this.mediaWrapper = mediaWrapper;
    }

    public String getMediaWrapperType() {
        return wrapperType;
    }

    public void setMediaWrapperType(String type) {
        this.wrapperType = type;
    }


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
