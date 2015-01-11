package models.mediawrappers;

import android.content.Context;
import android.os.Bundle;

import models.apiwrappers.CallbackInterface;
import models.mediaModels.Song;

/**
 * Created by charlotte on 06.12.14.
 *
 * @author charlotte
 *         We have a callback function for remote streaming.
 */
public abstract class RemoteFileStreamingMediaWrapper extends FileStreamingMediaWrapper implements CallbackInterface {

    public static final String DEFAULT_CALLBACK = "default_callback";


    public RemoteFileStreamingMediaWrapper(Context context, String playPath) {
        super(context, playPath);
    }

    public RemoteFileStreamingMediaWrapper(Context context, Song song) {
        super(context, song);
    }


    @Override
    public abstract void processWebCallResult(String result, String callback, Bundle data);

}
