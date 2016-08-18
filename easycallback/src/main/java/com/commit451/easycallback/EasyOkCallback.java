package com.commit451.easycallback;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Like {@link EasyCallback} but geared toward an OkHttp {@link Callback}. This callback also automatically posts on the Main thread by default for convenience
 */
public abstract class EasyOkCallback implements Callback {

    private static Handler sMainHandler;

    private static Handler getMainHandler() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
        return sMainHandler;
    }

    private Call mCall;
    private boolean mCallbackOnMainThread;

    public EasyOkCallback() {
        this(true);
    }

    public EasyOkCallback(boolean callbackOnMainThread) {
        mCallbackOnMainThread = callbackOnMainThread;
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

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        mCall = call;
        if (!response.isSuccessful()) {
            callFailure(new HttpException(response.code(), response.body()));
            return;
        }
        if (response.body() == null) {
            callFailure(new NullBodyException());
            return;
        }
        callSuccess(response);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mCall = call;
        failure(e);
    }

    @NonNull
    public Call getCall() {
        return mCall;
    }

    private void callSuccess(final Response response) {
        if (mCallbackOnMainThread) {
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
        if (mCallbackOnMainThread) {
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
