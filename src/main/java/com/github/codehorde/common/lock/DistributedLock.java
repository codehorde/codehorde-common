package com.github.codehorde.common.lock;

import java.util.concurrent.TimeUnit;

/**
 * DistributedLock
 * <p>
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
public interface DistributedLock {
    void lock() throws LockingException;

    boolean tryLock(long timeout, TimeUnit unit);

    void unlock() throws LockingException;
}
