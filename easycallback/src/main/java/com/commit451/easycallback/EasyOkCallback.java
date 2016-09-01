package com.commit451.easycallback;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;


/**
 * Like {@link EasyCallback} but geared toward an OkHttp {@link Callback}.
 */
public abstract class EasyOkCallback implements Callback {

    private Call call;
    private boolean allowNullBodies;
    private Executor executor;

    /**
     * Create an easy callback
     */
    public EasyOkCallback() {
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
     * Allows specification of if you want the callback to consider null bodies as a {@link NullBodyException}. Default is false
     * @param allowNullBodies true if you want to allow null bodies, false if you want exceptions throw on null bodies
     * @return this
     */
    public EasyOkCallback allowNullBodies(boolean allowNullBodies) {
        this.allowNullBodies = allowNullBodies;
        return this;
    }

    /**
     * Set the executor to have this callback call back on. Note: Overrides whatever you have set
     * on {@link OkHttpClient.Builder#dispatcher()}. If you want all calls to call back on the main
     * thread, consider overriding the {@link OkHttpClient#dispatcher()}. You can easily callback on
     * the Main (UI) thread by getting the default Main thread executor from Retrofit using
     * {@link RetrofitUtil#createDefaultCallbackExecutor()}
     * @param executor the executor to call back on
     * @return this
     */
    public EasyOkCallback executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public void onResponse(Call call, Response response) {
        this.call = call;
        if (!response.isSuccessful()) {
            postFailure(new HttpException(response));
            return;
        }
        if (response.body() == null && !allowNullBodies) {
            postFailure(new NullBodyException());
            return;
        }
        postSuccess(response);
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

    private void postSuccess(final Response response) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    success(response);
                }
            });
        } else {
            success(response);
        }
    }

    private void postFailure(final Exception e) {
        if (executor != null) {
            executor.execute(new Runnable() {
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
