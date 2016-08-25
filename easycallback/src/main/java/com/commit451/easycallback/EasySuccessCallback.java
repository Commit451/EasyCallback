package com.commit451.easycallback;

/**
 * Like {@link EasyCallback} but only alerts {@link #success(Object)}
 */
public abstract class EasySuccessCallback<T> extends EasyCallback<T> {

    @Override
    public void failure(Throwable t) {
        //noop
    }
}
