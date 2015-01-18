package models.mediawrappers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import models.mediaModels.Song;

/**
 * Created by lotta on 02.12.14.
 * @author charlotte
 *
 * For playing songs locally.
 */
public class LocalFileStreamingMediaWrapper extends FileStreamingMediaWrapper {

    public static final String TAG = "main.java.models.mediawrappers.LocalFileStreamingMediaWrapper";


    public LocalFileStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
        //computePlayPath(song);
    }

    public static void computePathMultiple (Context context, ArrayList<Song> songs) {
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        String songsString = "(";
        for (int i = 0; i < songs.size(); i++) {
            if (i != 0) {
                songsString += ", ";
            }
            Song song = songs.get(i);
            songsString += song.getArtist() + " - " + song.getSongname();
        }
        songsString += ")";

        String where = MediaStore.Audio.Media.ARTIST + " || ' - ' || " + MediaStore.Audio.Media.TITLE + " IN " + songsString;

        Cursor q = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, where, null, MediaStore.Audio.Media.TITLE);

        try {
            while (q.moveToNext()) {
                Log.v("", "compute playpath path: " + q.getString(0) + "\n");
            }
        } finally {
            if (q != null) {
                q.close();
            }
        }
    }

    @Override
    public void computePlayPath(Song song) {
        Log.v(TAG, "start playpath computation for song: " + song.toString());
        String path = "";
        String[] projection = {
                MediaStore.Audio.Media.DATA};

        String where = MediaStore.Audio.Media.TITLE + " LIKE ? and " + MediaStore.Audio.Media.ARTIST + " LIKE ?";
        String[] params = new String[]{"%" + song.getSongname() + "%", "%" + song.getArtist() + "%"};

        Cursor q = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, where, params, MediaStore.Audio.Media.TITLE);

        if (q != null) {
            try {
                while (q.moveToNext()) {
                    path = q.getString(0);
                    Log.v("", "compute playpath path: " + q.getString(0) + "\n");
                }
            } finally {
                q.close();
            }
        }

        this.setPlayPath(path);
    }

    @Override
    public boolean lookForSong() {
                computePlayPath(getSong());
                if (getPlayPath() == null || getPlayPath().equals("")) {
                    sendSongAvailableIntent(false);
                } else {
                    sendSongAvailableIntent(true);
                }

        return true;
    }
}
