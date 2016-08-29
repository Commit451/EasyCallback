package com.commit451.easycallback;


/**
 * Weird way to make Assert work in non-synchronous callbacks
 */
public class FailureHolder {
    String message;
    boolean failure;
    Throwable throwable;
}
