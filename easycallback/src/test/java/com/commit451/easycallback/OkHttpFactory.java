package com.commit451.easycallback;


import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Create a properly configured {@link okhttp3.OkHttpClient}
 */
public class OkHttpFactory {

    public static OkHttpClient create() {
        //So that the callbacks will execute properly
        ExecutorService executorService = MoreExecutors.newDirectExecutorService();
        Dispatcher dispatcher = new Dispatcher(executorService);
        return new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}
