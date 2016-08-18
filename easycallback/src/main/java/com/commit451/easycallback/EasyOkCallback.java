package com.commit451.easycallback;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Like {@link EasyCallback} but geared toward an OkHttp {@link Callback}
 */
public abstract class EasyOkCallback implements Callback {

    private Call mCall;

    /**
     * Called on success.
     * @param response the response
     */
    public abstract void success(@NonNull Response response);

    /**
     * Called on failure.
     * @param t the exception
     */
    public abstract void failure(Throwable t);

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        mCall = call;
        if (!response.isSuccessful()) {
            failure(new HttpException(response.code(), response.body()));
            return;
        }
        if (response.body() == null) {
            failure(new NullBodyException());
            return;
        }
        success(response);
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
}
