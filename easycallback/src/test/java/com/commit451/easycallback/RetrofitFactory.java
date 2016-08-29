package com.commit451.easycallback;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Creates properly configured Retrofit instance for testing
 */
public class RetrofitFactory {

    public static <T> T create(String baseUrl, Class<T> clazz) {
        OkHttpClient client = OkHttpFactory.create();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return restAdapter.create(clazz);
    }
}
