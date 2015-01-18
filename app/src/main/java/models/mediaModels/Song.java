package models.mediaModels;

import java.util.ArrayList;
import java.util.HashMap;

import models.mediawrappers.AbstractMediaWrapper;
import models.playlist.PlaylistsManager;

/**
 * Created by lotta on 02.12.14.
 *
 * @author charlotte
 *         <p/>
 *         Represents a song.
 */


public class Song {
    public static final String MEDIA_WRAPPER_LOCAL_FILE = "local files";
    public static final String MEDIA_WRAPPER_REMOTE_SOUNDCLOUD = "Soundcloud";
    public static final String MEDIA_WRAPPER_SPOTIFY = "Spotify";
    public static final String MEDIA_WRAPPER_NONE="no-mediawrapper";
    public static final String MEDIA_WRAPPER_NOT_SET="mediawrapper_not_set";
    private static final Object countLock = new Object();
    private static int currentSongID = 0;
    private String artist;
    private String songname;
    private transient AbstractMediaWrapper mediaWrapper;
    private transient String wrapperType;
    private transient boolean notPlayable = false;
    private int songID = -1;
    private int length = -1;

    private transient ArrayList<Listener> observers;

    /**
     * Constructor for Song with songname and artist.
     *
     * @param artist
     * @param songname
     */
    private Song(String artist, String songname) {
        this.artist = artist;
        this.songname = songname;
    }

    /**
     * Constructor for Song with songname, artist and media wraper type.
     *
     * @param artist
     * @param songname
     * @param wrapperType
     */
    private Song(String artist, String songname, String wrapperType) {
        this.artist = artist;
        this.songname = songname;
        this.wrapperType = wrapperType;
    }

    public Song(String artist, String songname, String mediaWrapperType, int id, String url, int length) {
        this(artist, songname, mediaWrapperType);
        this.songID = id;
        this.setSongUrl(url);
        this.length = length;
    }

    public void addObserver(Listener observer) {
        if (observers == null) observers = new ArrayList<>();
        this.observers.add(observer);
    }
    public void removeObserver(Listener observer) {
        if (observers == null) observers = new ArrayList<>();
        this.observers.remove(observer);
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

    public int getId() {
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
//        notifyChange();
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
        PlaylistsManager.getInstance().saveSong(this);
        notifyChange();
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
        PlaylistsManager.getInstance().saveSong(this);
        notifyChange();
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
//        notifyChange(true);
    }

    public int getLength() {
        return this.length; //unknown
    }

    public void setLength(int length) {
        this.length = length;
        PlaylistsManager.getInstance().saveSong(this);
        notifyChange();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            Song songObject = (Song) o;
            int id = getId();
            if (id != -1 && songObject.getId() == id) {
                return true;
            }
            return songObject.getSongname().contentEquals(this.getSongname()) &&
                   songObject.getArtist().contentEquals(this.getArtist());
        } else {
            return false;
        }
    }

    public boolean isNotPlayable() {
        return notPlayable;
    }

    public void setNotPlayable(boolean notPlayable) {
        this.notPlayable = notPlayable;
        notifyChange();
    }

    private void notifyChange() {
        if (observers != null) {
            for (Listener observer : observers) {
                observer.onSongChange();
            }
        }
    }

    public interface Listener {
        void onSongChange();
    }


    public static class Builder {

        private static HashMap<Integer, Song> songs = new HashMap<>();

        public static Song getSong(String artist, String songname) {
            //try to find Song in Db and find id for example (db will call getSongDb)
            Song song = PlaylistsManager.getInstance().getSong(artist, songname);
            // if no song is found, create new unsaved one when song is added to a playlist, which will be saved in DB, song will automatically be saved in db too
            if (song == null && artist != null && artist.length() > 0 && songname != null && songname.length() > 0) {

                song = new Song(artist, songname);
            }
            return song;
        }

        /**this function should only be used by PlaylistDatabaseHandler, because it doesn't load songs from db (it would cause an endless loop)*/
        public static Song getSongDb(int id, String artist, String songname, String mediaWrapperType, String url, int length) {
            Song song = songs.get(id);
            if (song == null && artist != null && artist.length() > 0 && songname != null && songname.length() > 0) {
                song = new Song(artist, songname, mediaWrapperType, id, url, length);
                //save unloaded songs to hashmap
                songs.put(song.getId(), song);
            }
            return song;
        }
    }
}
