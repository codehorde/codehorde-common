package com.github.codehorde.common.lock;

/**
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
public class LockingException extends RuntimeException {
    public LockingException(String msg, Exception e) {
        super(msg, e);
    }

    public LockingException(String msg) {
        super(msg);
    }
}