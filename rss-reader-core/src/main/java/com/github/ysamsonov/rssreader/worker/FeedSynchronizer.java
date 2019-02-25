package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import com.github.ysamsonov.rssreader.event.CreateFeedEvent;
import com.github.ysamsonov.rssreader.event.DeleteFeedEvent;
import com.github.ysamsonov.rssreader.event.EditFeedEvent;
import com.github.ysamsonov.rssreader.event.SwitchStateFeedEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class FeedSynchronizer {

    private final DelayParser delayParser = new DelayParser();

    private final ScheduledExecutorService executorService;

    private final FeedSyncTaskFactory syncTaskFactory = new FeedSyncTaskFactory();

    // feed unique name -> future
    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    // file name -> lock
    private final Map<String, ReentrantLock> writerLocks = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    public FeedSynchronizer(int poolSize) {
        this.executorService = Executors.newScheduledThreadPool(poolSize);
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

    public void onShutdown() {
        this.executorService.shutdown();
    }

    public void onCreateFeed(CreateFeedEvent event) {
        if (!event.getFeedConfig().isEnabled()) {
            return;
        }

        synchronized (monitor) {
            addFeed(event.getReaderConfig(), event.getFeedConfig());
        }
    }

    public void onEditFeed(EditFeedEvent event) {
        synchronized (monitor) {
            deleteFeed(event.getFeedConfig());
            addFeed(event.getReaderConfig(), event.getFeedConfig());
        }
    }

    public void onDeleteFeed(DeleteFeedEvent event) {
        synchronized (monitor) {
            deleteFeed(event.getFeedConfig());
        }
    }

    public void onSwitchStateFeed(SwitchStateFeedEvent event) {
        // delete or create
        synchronized (monitor) {
            if (!event.isEnabled()) {
                deleteFeed(event.getFeedConfig());
            }
            else {
                deleteFeed(event.getFeedConfig());
                addFeed(event.getReaderConfig(), event.getFeedConfig());
            }
        }
    }

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

    private void deleteFeed(FeedConfig feed) {
        ScheduledFuture<?> future = runningTasks.get(feed.getUrl());
        if (future == null) {
            return;
        }

        // TODO: may be false?
        future.cancel(true);
    }

    private void cleanupWriterLocks() {
        // TODO: cleanup
    }
}
