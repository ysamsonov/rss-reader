package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.ReaderConfig;

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

    // feed unique name -> future
    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    // file name -> lock
    private final Map<String, ReentrantLock> writerLocks = new ConcurrentHashMap<>();

    public FeedSynchronizer(int poolSize) {
        this.executorService = Executors.newScheduledThreadPool(poolSize);
    }

    public synchronized void update(ReaderConfig config) {
        for (var feed : config.getFeeds()) {
            final var writeLock = writerLocks.computeIfAbsent(feed.getFileName(), $ -> new ReentrantLock());
            final var task = new FeedSyncTask(feed, writeLock);

            final var delay = delayParser.parse(Optional.ofNullable(feed.getFetchTime()).orElse(config.getFetchTime()));

            final var scheduledFuture = executorService.scheduleWithFixedDelay(
                task,
                0,
                delay.getDelay(),
                delay.getUnit()
            );

            runningTasks.put(feed.getUrl(), scheduledFuture);
        }
    }
}
