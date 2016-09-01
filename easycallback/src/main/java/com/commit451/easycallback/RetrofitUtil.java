package com.commit451.easycallback;

import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import retrofit2.InternalRetrofitUtil;
import retrofit2.Retrofit;

/**
 * Utilities related to Retrofit
 */
public class RetrofitUtil {

    /**
     * Creates a new instance of the Callback executor that Retrofit uses. Typically used
     * if you want to set the {@link OkHttpClient#dispatcher()} to callback on the main thread. You
     * can also fetch this using {@link Retrofit#callbackExecutor()}, but this is still provided in
     * case you are going to tell Retrofit to call back on another executor and still want access
     * to its normal Main (UI) thread executor
     * @return a new executor configured the same way it is configured by default within Retrofit
     */
    public static Executor createDefaultCallbackExecutor() {
        return InternalRetrofitUtil.defaultCallbackExecutor();
    }
}
