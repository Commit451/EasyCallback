package com.commit451.easycallback;

/**
 * Represents that the body was null
 */
public class NullBodyException extends Exception {

    @Override
    public String getMessage() {
        return "The Retrofit body was null";
    }
}
