package com.commit451.easycallback;


import org.robolectric.util.concurrent.RoboExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Create a properly configured {@link okhttp3.OkHttpClient}
 */
public class OkHttpFactory {

    public static OkHttpClient create() {
        //So that the callbacks will execute properly
        Dispatcher dispatcher = new Dispatcher(new RoboExecutorService());
        return new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}
