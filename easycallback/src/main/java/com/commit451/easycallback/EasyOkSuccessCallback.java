package com.commit451.easycallback;

import okhttp3.Response;

/**
 * Like {@link EasyOkCallback} but only alerts {@link #success(Response)}
 */
public abstract class EasyOkSuccessCallback extends EasyOkCallback {

    @Override
    public void failure(Throwable t) {
        //noop
    }
}
