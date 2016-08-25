package com.commit451.easycallback;

import android.support.annotation.NonNull;

/**
 * Like {@link EasyCallback} but only alerts {@link #failure(Throwable)}
 */
public abstract class EasyFailureCallback<T> extends EasyCallback<T> {

    @Override
    public void success(@NonNull T response) {
        //noop
    }
}
