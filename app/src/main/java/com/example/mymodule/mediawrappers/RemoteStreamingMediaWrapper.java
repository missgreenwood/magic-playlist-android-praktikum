package com.example.mymodule.mediawrappers;

import android.content.Context;

import com.example.mymodule.mymodule.app.Song;

/**
 * Created by lotta on 02.12.14.
 */
public class RemoteStreamingMediaWrapper extends FileStreamingMediaWrapper {
    public RemoteStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public RemoteStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
    }

    @Override
    protected String computePlayPath(Song song) {
        return getPlayPath();
    }


}
