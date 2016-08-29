package com.commit451.easycallback;


import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * A Util to do things with OkHttp
 */
public class OkUtil {

    /**
     * Convert the response body to a string, using {@link ResponseBody#string()}, taking care of exceptions and closing the body
     * @param responseBody the body, which will be closed
     * @return a string, or null if there is an exception
     */
    @Nullable
    public static String toString(ResponseBody responseBody) {
        try {
            String body = responseBody.string();
            responseBody.close();
            return body;
        } catch (IOException e) {
            responseBody.close();
            return null;
        }
    }

    /**
     * Convert the response body to an input stream, using {@link ResponseBody#byteStream()}, taking care of closing the body
     * @param responseBody the body, which will be closed
     * @return an {@link InputStream} of the response body
     */
    public static InputStream toInputStream(ResponseBody responseBody) {
        InputStream stream = responseBody.byteStream();
        responseBody.close();
        return stream;
    }
}
