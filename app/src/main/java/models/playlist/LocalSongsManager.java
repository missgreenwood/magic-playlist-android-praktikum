package models.playlist;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collection;

import models.mediaModels.Song;
import models.mediawrappers.LocalFileStreamingMediaWrapper;

/**
 * Created by TheDaAndy on 12.01.2015.
 *
 */
public class LocalSongsManager {
    private static LocalSongsManager instance = new LocalSongsManager();
    private Context context;

    public static LocalSongsManager getInstance() {
        return instance;
    }

    private LocalSongsManager() {
    }

    public void setContext (Context context) {
        this.context = context;
    }

    public ArrayList<Song> getAllTracks ()
    {
        if (context == null) {
            return null;
        }

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= ?";
        String[] selectionArgs = {"0"};
        return getSongs(selection, selectionArgs);
    }

    public ArrayList<Song> getFilterTracks(String searchString)
    {
        if (context == null || searchString == null || searchString.isEmpty()) {
            return getAllTracks();
        }

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= ? and " + MediaStore.Audio.Media.TITLE + " LIKE ?";
        searchString = "%" + searchString + "%";
        String[] selectionArgs = {"0", searchString};

        return getSongs(selection, selectionArgs);
    }

    private Song getSongOfCursor(Cursor cur) {
        String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String url = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
        int length = Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION))) / 1000;

        Song song = Song.Builder.getSong(artist, title);
        song.setLength(length);

        LocalFileStreamingMediaWrapper wrapper = new LocalFileStreamingMediaWrapper(context, song);
        wrapper.setPlayPath(url);

        song.setMediaWrapper(wrapper);
        return song;
    }

    private ArrayList<Song> getSongs(String selection, String[] selectionArgs) {
        ArrayList<Song> foundTracks = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        int count;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    foundTracks.add(getSongOfCursor(cur));
                }
            }
        }
        cur.close();
        return foundTracks;
    }

    public ArrayList<String> getAllArtists()
    {
        return getArtists(null);
    }

    public ArrayList<String> getArtists(String searchString) {
        ArrayList<String> artists = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection;
        String[] selectionArgs;
        if (searchString != null && !searchString.isEmpty()) {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!= ? and " + MediaStore.Audio.Media.ARTIST + " LIKE ?";
            searchString = "%" + searchString + "%";
            selectionArgs = new String[] {"0", searchString};
        } else {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!= ?";
            selectionArgs = new String[] {"0"};
        }

        String[] projection = {
                MediaStore.Audio.Media.ARTIST,
        };
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        Cursor cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        int count;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if (!artists.contains(artist)) {
                        artists.add(artist);
                    }
                }
            }
        }
        cur.close();
        return artists;
    }
}
