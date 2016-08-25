package com.commit451.easycallback;

import android.support.annotation.NonNull;

import okhttp3.Response;

/**
 * Like {@link EasyOkCallback} but only alerts {@link #failure(Throwable)}
 */
public abstract class EasyOkFailureCallback extends EasyOkCallback {

    @Override
    public void success(@NonNull Response response) {
        //noop
    }
}
