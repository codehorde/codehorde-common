package com.github.codehorde.common.lock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
public class DistributedLockTest {
    private static final String LOCK_PATH = "/test/lock";

    private ZooKeeperClient zkClient;

    @Before
    public void mySetUp() throws Exception {
        zkClient = createZkClient();
    }

    @Test
    public void testFailDoubleLock() {
        DistributedLock lock = new DistributedLockImpl(zkClient, LOCK_PATH);
        lock.lock();
        try {
            lock.lock();
            fail("Exception expected!");
        } catch (LockingException e) {
            // expected
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testFailUnlock() {
        DistributedLock lock = new DistributedLockImpl(zkClient, LOCK_PATH);
        try {
            lock.unlock();
            fail("Expected exception while trying to unlock!");
        } catch (LockingException e) {
            // success
        }
    }

    @Test
    public void testTwoLocks() {
        DistributedLock lock1 = new DistributedLockImpl(zkClient, LOCK_PATH);
        DistributedLock lock2 = new DistributedLockImpl(zkClient, LOCK_PATH);
        lock1.lock();
        List<String> children = expectZkNodes(LOCK_PATH);
        assertEquals("One child == lock held!", children.size(), 1);
        lock1.unlock();
        // check no locks held/empty children
        children = expectZkNodes(LOCK_PATH);
        assertEquals("No children, no lock held!", children.size(), 0);
        lock2.lock();
        children = expectZkNodes(LOCK_PATH);
        assertEquals("One child == lock held!", children.size(), 1);
        lock2.unlock();
    }

    @Test
    public void testTwoLocksFailFast() {
        DistributedLock lock1 = new DistributedLockImpl(zkClient, LOCK_PATH);
        DistributedLock lock2 = new DistributedLockImpl(zkClient, LOCK_PATH);
        lock1.lock();
        boolean acquired = lock2.tryLock(1000, TimeUnit.MILLISECONDS);
        assertFalse("Couldn't acquire lock because it's currently held", acquired);
        lock1.unlock();
        lock2.unlock();
    }

    @Test
    @Ignore("pending: <http://jira.local.twitter.com/browse/RESEARCH-49>")
    public void testMultiConcurrentLocking() throws Exception {
        //TODO(Florian Leibert): this is a bit janky, so let's replace it.
        for (int i = 0; i < 5; i++) {
            testConcurrentLocking();
        }
        mySetUp();
    }

    @Test
    public void testConcurrentLocking() throws Exception {
        ZooKeeperClient zk1 = createZkClient();
        ZooKeeperClient zk2 = createZkClient();
        ZooKeeperClient zk3 = createZkClient();

        final DistributedLock lock1 = new DistributedLockImpl(zk1, LOCK_PATH);
        final DistributedLock lock2 = new DistributedLockImpl(zk2, LOCK_PATH);
        final DistributedLock lock3 = new DistributedLockImpl(zk3, LOCK_PATH);
        Callable<Object> t1 = new Callable<Object>() {
            @Override
            public Object call() throws InterruptedException {
                lock1.lock();
                try {
                    Thread.sleep(5000);
                } finally {
                    lock1.unlock();
                }
                return new Object();
            }
        };

        Callable<Object> t2 = new Callable<Object>() {
            @Override
            public Object call() throws InterruptedException {
                lock2.lock();
                try {
                    Thread.sleep(5000);
                } finally {
                    lock2.unlock();
                }
                return new Object();
            }
        };

        Callable<Object> t3 = new Callable<Object>() {
            @Override
            public Object call() throws InterruptedException {
                lock3.lock();
                try {
                    Thread.sleep(5000);
                } finally {
                    lock3.unlock();
                }
                return new Object();
            }
        };

        //TODO(Florian Leibert): remove this executors stuff and use a latch instead.
        ExecutorService ex = Executors.newCachedThreadPool();
        @SuppressWarnings("unchecked") List<Callable<Object>> tlist = Arrays.asList(t1, t2, t3);
        ex.invokeAll(tlist);
        assertTrue("No Children left!", expectZkNodes(LOCK_PATH).size() == 0);
    }

    private ZooKeeperClient createZkClient() {
        return new ZooKeeperClient(30000,
                InetSocketAddress.createUnresolved("127.0.0.1", 2181),
                InetSocketAddress.createUnresolved("127.0.0.1", 2182),
                InetSocketAddress.createUnresolved("127.0.0.1", 2183));
    }

    protected List<String> expectZkNodes(String path) {
        try {
            List<String> children = zkClient.get().getChildren(path, null);
            return children;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
