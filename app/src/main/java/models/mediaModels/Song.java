package models.mediaModels;

import android.util.Log;

import models.mediawrappers.AbstractMediaWrapper;
import tests.R;

/**
 * Created by lotta on 02.12.14.
 *
 * @author charlotte
 *         <p/>
 *         Represents a song.
 */


public class Song {
    public static final String MEDIA_WRAPPER_LOCAL_FILE = "local_file";
    public static final String MEDIA_WRAPPER_REMOTE_SOUNDCLOUD = "remote_soundcloud";
    public static final String MEDIA_WRAPPER_SPOTIFY = "remote_spotify";
    private static final Object countLock = new Object();
    private static int currentSongID = 0;
    private String artist;
    private String songname;
    private AbstractMediaWrapper mediaWrapper;
    private String wrapperType;
    private int songID;
    private int length = -1;

    /**
     * Constructor for Song with songname and artist.
     *
     * @param artist
     * @param songname
     */
    public Song(String artist, String songname) {
        this.artist = artist;
        this.songname = songname;
        synchronized (countLock) {
            currentSongID++;
        }
        this.songID = currentSongID;

        Log.d("", "created song object with " + artist + songname + " with id: " + currentSongID);
    }

    public Song ()
    {
        this("", "");
        //informations are given afterwards, needed in PlaylistFileHandler
    }

    /**
     * Constructor for Song with songname, artist and media wraper type.
     *
     * @param artist
     * @param songname
     * @param wrapperType
     */
    public Song(String artist, String songname, String wrapperType) {
        this.artist = artist;
        this.songname = songname;
        this.wrapperType = wrapperType;
    }

    @Override
    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", songname='" + songname + '\'' +
                ", songID=" + songID +
                ", wrapperType='" + wrapperType + '\'' +
                '}';
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public AbstractMediaWrapper getMediaWrapper() {
        return mediaWrapper;
    }

    /**
     * Sets the corresponding media wrapper for a song object; this shouldn't have to be
     * called explicitly from outside PlayeQueue class.
     *
     * @param mediaWrapper
     */
    public void setMediaWrapper(AbstractMediaWrapper mediaWrapper) {
        this.mediaWrapper = mediaWrapper;
    }

    public String getMediaWrapperType() {
        return wrapperType;
    }

    /**
     * Sets the media wrapper type for song. This has to be done if media wrapper type
     * has not yet been set inside the constructor.
     *
     * @param type
     */
    public void setMediaWrapperType(String type) {
        this.wrapperType = type;
    }


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

    public String getSongUrl()
    {
        String path = mediaWrapper != null ? mediaWrapper.getPlayPath() : getMediaWrapperType();
        return path;
    }

    /**
     * returns a filePath if song exists locally else returns remote mediastream
     */
    public void setSongUrl(String url) {
//        this.url = url;
    }

    public int getLength() {
        return this.length; //unknown
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            Song songObject = (Song) o;
            return songObject.getSongname().contentEquals(this.getSongname()) &&
                   songObject.getArtist().contentEquals(this.getArtist());
        } else {
            return false;
        }
    }
}
