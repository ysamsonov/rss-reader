package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.worker.impl.FeedFilterProcessor;
import com.github.ysamsonov.rssreader.worker.impl.FileFeedWriter;
import com.github.ysamsonov.rssreader.worker.impl.UrlFeedReader;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class BaseFeedSyncTaskFactory implements FeedSyncTaskFactory {

    @Override
    public FeedSyncTask create(FeedConfig feedConfig, ReentrantLock writeLock) {
        var reader = new UrlFeedReader(feedConfig);
        var processor = new FeedFilterProcessor(feedConfig);
        var writer = new FileFeedWriter(feedConfig, writeLock);

        return new FeedSyncTask(feedConfig, reader, processor, writer);
    }
}
