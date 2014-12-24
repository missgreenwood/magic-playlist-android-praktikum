package com.example.mymodule.metadatawrappers;

import android.util.Log;

import com.example.mymodule.apiwrappers.CallbackInterface;

/**
 * Created by charlotte on 19.12.14.
 */
public abstract class AbstractMetadataWrapper implements CallbackInterface {


    @Override
    public abstract void processWebCallResult(String result, String callback);
}
