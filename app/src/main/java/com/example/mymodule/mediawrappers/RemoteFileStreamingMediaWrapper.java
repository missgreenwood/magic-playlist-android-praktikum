package com.example.mymodule.mediawrappers;

import android.content.Context;

import com.example.mymodule.mymodule.app.Song;

/**
 * Created by charlotte on 06.12.14.
 */
public abstract class RemoteFileStreamingMediaWrapper extends FileStreamingMediaWrapper {


    public RemoteFileStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public RemoteFileStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
    }


    public abstract void processWebCallResult(String result);

}
