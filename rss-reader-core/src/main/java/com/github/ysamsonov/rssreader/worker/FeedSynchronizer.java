package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import com.github.ysamsonov.rssreader.event.CreateFeedEvent;
import com.github.ysamsonov.rssreader.event.DeleteFeedEvent;
import com.github.ysamsonov.rssreader.event.EditFeedEvent;
import com.github.ysamsonov.rssreader.event.SwitchStateFeedEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides startup and monitoring of feed synchronization tasks
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class FeedSynchronizer {

    private final DelayParser delayParser = new DelayParser();

    private final ScheduledExecutorService executorService;

    private final FeedSyncTaskFactory syncTaskFactory;

    // feed unique name -> future
    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    // file name -> lock
    private final Map<String, ReentrantLock> writerLocks = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    public FeedSynchronizer(int poolSize, FeedSyncTaskFactory syncTaskFactory) {
        this.executorService = Executors.newScheduledThreadPool(poolSize);
        this.syncTaskFactory = syncTaskFactory;
    }

    /**
     * Note: no need synchronization because it run synchronously during app context building.
     */
    public void onStart(ReaderConfig config) {
        for (var feed : config.getFeeds()) {
            if (!feed.isEnabled()) {
                continue;
            }

            addFeed(config, feed);
        }
    }

    /**
     * Finalize all task before shutdown
     */
    public void onShutdown() {
        log.info("Shutdown executor service");
        this.executorService.shutdown();
    }

    /**
     * Create task for new feed and schedule it
     */
    public void onCreateFeed(CreateFeedEvent event) {
        if (!event.getFeedConfig().isEnabled()) {
            return;
        }

        synchronized (monitor) {
            addFeed(event.getReaderConfig(), event.getFeedConfig());
        }
    }

    /**
     * Handle modify feed event
     */
    public void onEditFeed(EditFeedEvent event) {
        synchronized (monitor) {
            deleteFeed(event.getFeedConfig(), event.getFileNames());
            addFeed(event.getReaderConfig(), event.getFeedConfig());
        }
    }

    /**
     * Handle delete feed event
     */
    public void onDeleteFeed(DeleteFeedEvent event) {
        synchronized (monitor) {
            deleteFeed(event.getFeedConfig(), event.getFileNames());
        }
    }

    /**
     * Handle switch state event, then create or delete feed
     */
    public void onSwitchStateFeed(SwitchStateFeedEvent event) {
        // delete or create
        synchronized (monitor) {
            if (!event.isEnabled()) {
                deleteFeed(event.getFeedConfig(), event.getFileNames());
            }
            else {
                deleteFeed(event.getFeedConfig(), event.getFileNames());
                addFeed(event.getReaderConfig(), event.getFeedConfig());
            }
        }
    }

    /**
     * Create task, writer lock for new feed, parse schedule config and schedule it
     */
    private void addFeed(ReaderConfig config, FeedConfig feed) {
        final var writeLock = writerLocks.computeIfAbsent(feed.getFileName(), $ -> new ReentrantLock());
        final var task = syncTaskFactory.create(feed, writeLock);

        final var delay = delayParser.parse(
            Optional.ofNullable(feed.getFetchTime()).orElse(config.getFetchTime())
        );

        final var scheduledFuture = executorService.scheduleWithFixedDelay(
            task,
            0,
            delay.getDelay(),
            delay.getUnit()
        );

        runningTasks.put(feed.getUrl(), scheduledFuture);
    }

    /**
     * Cancel scheduled task for feed
     */
    private void deleteFeed(FeedConfig feed, Set<String> fileNames) {
        ScheduledFuture<?> future = runningTasks.get(feed.getUrl());
        if (future == null) {
            return;
        }

        future.cancel(true);
        runningTasks.remove(feed.getUrl());
        cleanupWriterLocks(fileNames);
    }

    private void cleanupWriterLocks(Set<String> fileNames) {
        HashSet<String> locksToRemove = new HashSet<>(writerLocks.keySet());
        locksToRemove.removeAll(fileNames);

        for (String lockName : locksToRemove) {
            writerLocks.remove(lockName);
        }
    }
}
