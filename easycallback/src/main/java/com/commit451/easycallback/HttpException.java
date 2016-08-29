package com.commit451.easycallback;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Represents an HTTP non 200 response from Retrofit
 */
public class HttpException extends Exception {

    private Response mResponse;
    private ResponseBody mErrorResponseBody;

    /**
     * Create an http exception assuming that {@link Response#body()} is the error body
     * @param response the response
     */
    public HttpException(Response response) {
        mResponse = response;
    }

    /**
     * Create an http exception with an explicit error body, typically used for Retrofit
     * @param response the response
     * @param errorResponseBody the error response body
     */
    public HttpException(Response response, ResponseBody errorResponseBody) {
        mResponse = response;
        mErrorResponseBody = errorResponseBody;
    }

    @Override
    public String getMessage() {
        return mResponse.message();
    }

    /**
     * Get the raw {@link Response} from OkHttp which forced this request to have an exception
     * @return the response
     */
    public Response response() {
        return mResponse;
    }

    /**
     * Get the error body
     * @return the error body
     */
    public ResponseBody errorBody() {
        if (mErrorResponseBody != null) {
            return mErrorResponseBody;
        }
        return mResponse.body();
    }
}