package com.commit451.easycallback;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Like {@link EasyCallback} but geared toward an OkHttp {@link Callback}.
 * This callback also automatically posts on the Main thread by default for convenience
 */
public abstract class EasyOkCallback implements Callback {

    private static Handler mainHandler;

    private static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    private Call call;
    private boolean callbackOnMainThread;
    private boolean allowNullBodies;

    /**
     * Create an easy callback
     */
    public EasyOkCallback() {
        callbackOnMainThread = true;
        allowNullBodies = false;
    }

    /**
     * Called on success.
     *
     * @param response the response
     */
    public abstract void success(@NonNull Response response);

    /**
     * Called on failure.
     *
     * @param t the exception
     */
    public abstract void failure(Throwable t);

    /**
     * Allows specification of if you want the callback on the main thread. Default is true
     * @param callbackOnMainThread true if you want it on the main thread, false if you want it on the background thread
     * @return this
     */
    public EasyOkCallback callbackOnMainThread(boolean callbackOnMainThread) {
        this.callbackOnMainThread = callbackOnMainThread;
        return this;
    }

    /**
     * Allows specification of if you want the callback to consider null bodies as a {@link NullBodyException}. Default is false
     * @param allowNullBodies true if you want to allow null bodies, false if you want exceptions throw on null bodies
     * @return this
     */
    public EasyOkCallback allowNullBodies(boolean allowNullBodies) {
        this.allowNullBodies = allowNullBodies;
        return this;
    }

    @Override
    public void onResponse(Call call, Response response) {
        this.call = call;
        if (!response.isSuccessful()) {
            callFailure(new HttpException(response));
            return;
        }
        if (response.body() == null && !allowNullBodies) {
            callFailure(new NullBodyException());
            return;
        }
        callSuccess(response);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        this.call = call;
        failure(e);
    }

    @NonNull
    public Call call() {
        return call;
    }

    private void callSuccess(final Response response) {
        if (callbackOnMainThread) {
            getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    success(response);
                }
            });
        } else {
            success(response);
        }
    }

    private void callFailure(final Exception e) {
        if (callbackOnMainThread) {
            getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    failure(e);
                }
            });
        } else {
            failure(e);
        }
    }
}
