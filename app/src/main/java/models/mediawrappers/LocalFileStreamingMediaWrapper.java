package models.mediawrappers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import models.mediaModels.Song;

/**
 * Created by lotta on 02.12.14.
 * @author charlotte
 *
 * For playing songs locally.
 */
public class LocalFileStreamingMediaWrapper extends FileStreamingMediaWrapper {

    public static final String TAG = "main.java.models.mediawrappers.LocalFileStreamingMediaWrapper";

    /*
    public LocalFileStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }*/

    public LocalFileStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
        //computePlayPath(song);
    }

    @Override
    public void computePlayPath(Song song) {
        Log.d(TAG, "start playpath computation for song: " + song.toString());
        String path = "";
        String[] projection = {
                MediaStore.Audio.Media.DATA};

        String where = MediaStore.Audio.Media.TITLE + " LIKE ? and " + MediaStore.Audio.Media.ARTIST + " LIKE ?";
        String[] params = new String[]{"%" + song.getSongname() + "%", "%" + song.getArtist() + "%"};

        Cursor q = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, where, params, MediaStore.Audio.Media.TITLE);

        try {
            while (q.moveToNext()) {
                path = q.getString(0);
                Log.d("", "compute playpath path: " + q.getString(0) + "\n");
            }
        } finally {
            q.close();
        }

        this.setPlayPath(path);
        //return path;
    }

    @Override
    public boolean lookForSong() {

        Log.d(TAG, "look for song: " + getSong().toString());

        computePlayPath(getSong());

        //TODO: in Methode


        Log.d(TAG, "local playpath: " + getPlayPath());

        if (getPlayPath() == null || getPlayPath().equals("")) {
            // intent.setAction(PlayQueue.SONG_NOT_AVAILABLE);
            //  Log.d(TAG, "send song not available");
            sendSongAvailableIntent(false);
        } else {
            //  intent.setAction(PlayQueue.SONG_AVAILABLE);
            //  Log.d(TAG, "send song available");
            sendSongAvailableIntent(true);

        }

        //  intent.putExtra(PlayQueue.SONG_ID, getSong().getSongID());
        //  context.sendBroadcast(intent);


        // play();

        return true;

    }


}
