package com.commit451.easycallback;


/**
 * Holds some successful class
 */
public class SuccessHolder<T> {
    T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
