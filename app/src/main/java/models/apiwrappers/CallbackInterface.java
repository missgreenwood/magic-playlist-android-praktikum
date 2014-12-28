package models.apiwrappers;

/**
 * Created by charlotte on 19.12.14.
 */
public interface CallbackInterface {

    public abstract void processWebCallResult(String result, String callback);
}
