package models.apiwrappers;

import android.os.Bundle;

/**
 * Created by charlotte on 19.12.14.
 *
 * @author charlotte
 */
public interface CallbackInterface {

    public abstract void processWebCallResult(String result, String callback, Bundle data);
}
