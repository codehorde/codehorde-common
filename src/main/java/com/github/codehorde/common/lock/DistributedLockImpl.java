package com.github.codehorde.common.lock;

import com.github.codehorde.common.tool.CompareUtils;
import com.github.codehorde.common.tool.ZooKeeperUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
@ThreadSafe
public class DistributedLockImpl implements DistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockImpl.class);

    public static final int ANY_VERSION = -1;

    private final ZooKeeperClient zkClient;
    private final String lockPath;
    private final ImmutableList<ACL> acl;

    private final AtomicBoolean aborted = new AtomicBoolean(false);
    private CountDownLatch syncPoint;
    private boolean holdsLock = false;
    private String currentId;
    private String currentNode;
    private String watchedNode;
    private LockWatcher watcher;

    /**
     * Equivalent to {@link #DistributedLockImpl(ZooKeeperClient, String, Iterable)} with a default
     * wide open {@code acl} ({@link ZooDefs.Ids#OPEN_ACL_UNSAFE}).
     */
    public DistributedLockImpl(ZooKeeperClient zkClient, String lockPath) {
        this(zkClient, lockPath, ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }

    /**
     * Creates a distributed lock using the given {@code zkClient} to coordinate locking.
     *
     * @param zkClient The ZooKeeper client to use.
     * @param lockPath The path used to manage the lock under.
     * @param acl      The acl to apply to newly created lock nodes.
     */
    public DistributedLockImpl(ZooKeeperClient zkClient, String lockPath, Iterable<ACL> acl) {
        this.zkClient = Preconditions.checkNotNull(zkClient);
        Preconditions.checkArgument(CompareUtils.isNotBlank(lockPath));
        this.lockPath = lockPath;
        this.acl = ImmutableList.copyOf(acl);
        this.syncPoint = new CountDownLatch(1);
    }

    private synchronized void prepare()
            throws ZooKeeperClient.ZooKeeperConnectionException, InterruptedException, KeeperException {

        ZooKeeperUtils.ensurePath(zkClient, acl, lockPath);
        logger.debug("Working with locking path: {}", lockPath);

        // Create an EPHEMERAL_SEQUENTIAL node.
        currentNode =
                zkClient.get().create(lockPath + "/member_", null, acl, CreateMode.EPHEMERAL_SEQUENTIAL);

        // We only care about our actual id since we want to compare ourselves to siblings.
        if (currentNode.contains("/")) {
            currentId = currentNode.substring(currentNode.lastIndexOf("/") + 1);
        }
        logger.debug("Received ID from zk: {}", currentId);
        this.watcher = new LockWatcher();
    }

    @Override
    public synchronized void lock() throws LockingException {
        if (holdsLock) {
            throw new LockingException("Error, already holding a lock. Call unlock first!");
        }
        try {
            prepare();
            watcher.checkForLock();
            syncPoint.await();
            if (!holdsLock) {
                throw new LockingException("Error, couldn't acquire the lock!");
            }
        } catch (InterruptedException e) {
            cancelAttempt();
            throw new LockingException("InterruptedException while trying to acquire lock!", e);
        } catch (KeeperException e) {
            // No need to clean up since the node wasn't created yet.
            throw new LockingException("KeeperException while trying to acquire lock!", e);
        } catch (ZooKeeperClient.ZooKeeperConnectionException e) {
            // No need to clean up since the node wasn't created yet.
            throw new LockingException("ZooKeeperConnectionException while trying to acquire lock", e);
        }
    }

    @Override
    public synchronized boolean tryLock(long timeout, TimeUnit unit) {
        if (holdsLock) {
            throw new LockingException("Error, already holding a lock. Call unlock first!");
        }
        try {
            prepare();
            watcher.checkForLock();
            boolean success = syncPoint.await(timeout, unit);
            if (!success) {
                return false;
            }
            if (!holdsLock) {
                throw new LockingException("Error, couldn't acquire the lock!");
            }
        } catch (InterruptedException e) {
            cancelAttempt();
            return false;
        } catch (KeeperException e) {
            // No need to clean up since the node wasn't created yet.
            throw new LockingException("KeeperException while trying to acquire lock!", e);
        } catch (ZooKeeperClient.ZooKeeperConnectionException e) {
            // No need to clean up since the node wasn't created yet.
            throw new LockingException("ZooKeeperConnectionException while trying to acquire lock", e);
        }
        return true;
    }

    @Override
    public synchronized void unlock() throws LockingException {
        if (currentId == null) {
            throw new LockingException("Error, neither attempting to lock nor holding a lock!");
        }
        Preconditions.checkNotNull(currentId);
        // Try aborting!
        if (!holdsLock) {
            aborted.set(true);
            logger.info("Not holding lock, aborting acquisition attempt! ephemeral node {}", currentId);
        } else {
            logger.info("Cleaning up this locks ephemeral node {}.", currentId);
            cleanup();
        }
    }

    //TODO(Florian Leibert): Make sure this isn't a runtime exception. Put exceptions into the token?

    private synchronized void cancelAttempt() {
        logger.info("Cancelling lock attempt!");
        cleanup();
        // Bubble up failure...
        holdsLock = false;
        syncPoint.countDown();
    }

    private void cleanup() {
        logger.info("Cleaning up ephemeral node {}!", currentId);
        Preconditions.checkNotNull(currentId);
        try {
            Stat stat = zkClient.get().exists(currentNode, false);
            if (stat != null) {
                zkClient.get().delete(currentNode, ANY_VERSION);
            } else {
                logger.warn("Called cleanup ephemeral node {} but nothing to cleanup!", currentNode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        holdsLock = false;
        aborted.set(false);
        currentId = null;
        currentNode = null;
        watcher = null;
        syncPoint = new CountDownLatch(1);
    }

    private class LockWatcher implements Watcher {

        private synchronized void checkForLock() {
            Preconditions.checkArgument(CompareUtils.isNotBlank(currentId));

            try {
                List<String> candidates = zkClient.get().getChildren(lockPath, null);
                ImmutableList<String> sortedMembers = Ordering.natural().immutableSortedCopy(candidates);

                // Unexpected behavior if there are no children!
                if (sortedMembers.isEmpty()) {
                    throw new LockingException("Error, member list is empty!");
                }

                int memberIndex = sortedMembers.indexOf(currentId);

                // If we hold the lock
                if (memberIndex == 0) {
                    holdsLock = true;
                    syncPoint.countDown();
                } else {
                    final String nextLowestNode = sortedMembers.get(memberIndex - 1);
                    logger.info("Current LockWatcher with ephemeral node {}, is " +
                            "waiting for {} to release lock.", currentId, nextLowestNode);

                    watchedNode = String.format("%s/%s", lockPath, nextLowestNode);
                    Stat stat = zkClient.get().exists(watchedNode, this);
                    if (stat == null) {
                        checkForLock();
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Current LockWatcher with ephemeral node {} " +
                        "got interrupted. Trying to cancel lock acquisition.", currentId, e);
                cancelAttempt();
            } catch (KeeperException e) {
                logger.warn("Current LockWatcher with ephemeral node {} " +
                        "got a KeeperException. Trying to cancel lock acquisition.", currentId, e);
                cancelAttempt();
            } catch (ZooKeeperClient.ZooKeeperConnectionException e) {
                logger.warn("Current LockWatcher with ephemeral node {} " +
                        "got a ConnectionException. Trying to cancel lock acquisition.", currentId, e);
                cancelAttempt();
            }
        }

        @Override
        public synchronized void process(WatchedEvent event) {
            // this handles the case where we have aborted a lock and deleted ourselves but still have a
            // watch on the nextLowestNode. This is a workaround since ZK doesn't support unsub.
            if (!event.getPath().equals(watchedNode)) {
                logger.info("Ignoring call for node:{} ", watchedNode);
                return;
            }
            //TODO(Florian Leibert): Pull this into the outer class.
            if (event.getType() == Watcher.Event.EventType.None) {
                switch (event.getState()) {
                    case SyncConnected:
                        // TODO(Florian Leibert): maybe we should just try to "fail-fast" in this case and abort.
                        logger.info("Reconnected...");
                        break;
                    case Expired:
                        logger.warn("Current ZK session expired! {} ", currentId);
                        cancelAttempt();
                        break;
                }
            } else if (event.getType() == Event.EventType.NodeDeleted) {
                checkForLock();
            } else {
                logger.warn("Unexpected ZK event: {}", event.getType().name());
            }
        }
    }
}
