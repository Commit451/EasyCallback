package com.commit451.easycallback;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Creates properly configured Retrofit instance for testing
 */
public class RetrofitFactory {

    public static <T> T create(String baseUrl, Class<T> clazz) {
        OkHttpClient client = OkHttpFactory.create();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .callbackExecutor(new Executor() {
                    @Override
                    public void execute(@NonNull Runnable command) {
                        command.run();
                    }
                })
                .addConverterFactory(GsonConverterFactory.create());
        return retrofitBuilder.build().create(clazz);
    }
}
