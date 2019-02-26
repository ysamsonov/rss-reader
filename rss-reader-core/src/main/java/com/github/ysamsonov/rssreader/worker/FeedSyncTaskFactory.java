package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Interface for {@link FeedSyncTask} factory
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
public interface FeedSyncTaskFactory {

    /**
     * Create {@link FeedSyncTask} based on feed configuration
     *
     * @param feedConfig - feed configuration for which the task is created
     * @param writeLock  - lock for avoiding concurrent writing to the storage
     * @return configured, reusable, and ready to run task
     */
    FeedSyncTask create(FeedConfig feedConfig, ReentrantLock writeLock);
}
