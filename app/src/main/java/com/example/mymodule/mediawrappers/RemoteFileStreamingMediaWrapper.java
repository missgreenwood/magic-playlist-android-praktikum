package com.example.mymodule.mediawrappers;

import android.content.Context;

import com.example.mymodule.apiwrappers.CallbackInterface;
import com.example.mymodule.mymodule.app.Song;

import java.util.List;

/**
 * Created by charlotte on 06.12.14.
 */
public abstract class RemoteFileStreamingMediaWrapper extends FileStreamingMediaWrapper implements CallbackInterface {

    public static final String DEFAULT_CALLBACK = "default_callback";


    public RemoteFileStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public RemoteFileStreamingMediaWrapper(Context context, List<Song> songs) {
        super(context, songs);
    }


    @Override
    public abstract void processWebCallResult(String result, String callback);

}
