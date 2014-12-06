package com.example.mymodule.mediawrappers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.mymodule.mymodule.app.Song;

/**
 * Created by lotta on 02.12.14.
 */
public class LocalFileStreamingMediaWrapper extends FileStreamingMediaWrapper {
    public LocalFileStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public LocalFileStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
       //computePlayPath(song);
    }

    @Override
    public void computePlayPath(Song song) {
        Log.d("", "start playpath computation");
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
                Log.d("", "compute playpath path: "+q.getString(0) + "\n");
            }
        } finally {
            q.close();
        }

        this.setPlayPath(path);
        //return path;
    }

    @Override
    public void playSong() {
        computePlayPath(getSong());
        play();


    }


}
