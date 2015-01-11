package models.metadatawrappers;

import android.os.Bundle;

import models.apiwrappers.CallbackInterface;

/**
 * Created by charlotte on 19.12.14.
 *
 * @author charlotte
 *         Can be used to get metadata e.g. from lastfm.
 */
public abstract class AbstractMetadataWrapper implements CallbackInterface {


    @Override
    public abstract void processWebCallResult(String result, String callback, Bundle data);
}
