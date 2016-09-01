package com.commit451.easycallback;


import okhttp3.Response;
import retrofit2.Call;

/**
 * Weird way to hold test data in non-synchronous callbacks
 */
public class TestHolder {
    String message;
    boolean failure;
    Throwable throwable;
    Response response;
    retrofit2.Response retrofitResponse;
    Call call;
}
