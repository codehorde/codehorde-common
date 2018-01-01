package com.github.codehorde.common.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 多个线程WaitPoint#await()阻塞在一个时间点，等待WaitPoint#release()通知，
 * 当WaitPoint.Sync中state为0所有线程解除阻塞状态
 * <p>
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
public class WaitPoint {

    private static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 198226493452014374L;

        Sync(int count) {
            setState(count);
        }

        protected int tryAcquireShared(int acquires) {
            return getState() == 0 ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            for (; ; ) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c - 1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;

    public WaitPoint(int count) {
        this.sync = new Sync(count);
    }

    public void await() throws InterruptedException {
        sync.acquireShared(1);
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void release() {
        sync.releaseShared(1);
    }
}