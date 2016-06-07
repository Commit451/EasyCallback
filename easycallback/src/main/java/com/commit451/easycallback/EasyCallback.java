package com.commit451.easycallback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An easier version of a Retrofit callback that simplifies
 * the response block so that you do not have to check
 * {@link Response#isSuccessful()}. You can still call {@link #getResponse()}
 * if you need it. If there is a HTTP error, {@link #failure(Throwable)}
 * will be called with a {@link HttpException}
 */
public abstract class EasyCallback<T> implements Callback<T> {

    @Nullable
    private Response<T> mResponse;
    private Call<T> mCall;

    /**
     * Called on success. You can still get the original {@link Response} via {@link #getResponse()}
     * @param response the response
     */
    public abstract void success(@NonNull T response);

    /**
     * Called on failure.
     * @param t the exception
     */
    public abstract void failure(Throwable t);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        mCall = call;
        mResponse = response;
        if (!response.isSuccessful()) {
            failure(new HttpException(response.code(), response.errorBody()));
            return;
        }
        if (response.body() == null) {
            failure(new NullBodyException());
            return;
        }
        success(response.body());
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        mCall = call;
        failure(t);
    }

    /**
     * Get the Retrofit response. If you are in the {@link #failure(Throwable)} block, this will be null if your failure was not an HTTP error, so beware
     * @return the retrofit response, if any exists
     */
    @Nullable
    public Response<T> getResponse() {
        return mResponse;
    }

    /**
     * Get the call that was originally made
     * @return the call
     */
    @NonNull
    public Call<T> getCall() {
        return mCall;
    }
}
