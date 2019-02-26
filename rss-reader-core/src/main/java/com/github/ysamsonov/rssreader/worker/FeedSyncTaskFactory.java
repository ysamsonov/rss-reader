package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
public interface FeedSyncTaskFactory {
    FeedSyncTask create(FeedConfig feedConfig, ReentrantLock writeLock);
}
