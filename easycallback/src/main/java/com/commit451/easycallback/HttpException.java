package com.commit451.easycallback;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Represents an HTTP non 200 response from Retrofit
 */
public class HttpException extends Exception {

    private Response response;
    private ResponseBody errorResponseBody;

    /**
     * Create an http exception assuming that {@link Response#body()} is the error body
     * @param response the response
     */
    public HttpException(Response response) {
        this.response = response;
        this.errorResponseBody = response.body();
    }

    /**
     * Create an http exception with an explicit error body, typically used for Retrofit
     * @param response the response
     * @param errorResponseBody the error response body
     */
    public HttpException(Response response, ResponseBody errorResponseBody) {
        this.response = response;
        this.errorResponseBody = errorResponseBody;
    }

    @Override
    public String getMessage() {
        return response.message();
    }

    /**
     * Get the raw {@link Response} from OkHttp which forced this request to have an exception
     * @return the response
     */
    public Response response() {
        return response;
    }

    /**
     * Get the error body. If using OkHttp, this will be the normal body. Be sure to call
     * {@link ResponseBody#close()} on the {@link ResponseBody} when you are done using it
     * @return the error body
     */
    public ResponseBody errorBody() {
        return errorResponseBody;
    }
}