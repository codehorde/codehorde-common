package com.github.codehorde.common.bean.support;

/**
 * Created by baomingfeng at 2018-04-28 16:04:14
 */
public class ValueHolder<T> {

    private volatile T value;

    public ValueHolder() {
    }

    public ValueHolder(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

}