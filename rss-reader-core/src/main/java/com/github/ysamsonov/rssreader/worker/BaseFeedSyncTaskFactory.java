package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.event.ApplicationEventPublisher;
import com.github.ysamsonov.rssreader.worker.impl.FeedFilterProcessor;
import com.github.ysamsonov.rssreader.worker.impl.FileFeedWriter;
import com.github.ysamsonov.rssreader.worker.impl.UrlFeedReader;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Default implementation of {@link FeedSyncTaskFactory} that read data from internet, filter by pubDate, and write to file
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@RequiredArgsConstructor
public class BaseFeedSyncTaskFactory implements FeedSyncTaskFactory {

    private final ApplicationEventPublisher eventPublisher;

    private final int writerBatchSize;

    @Override
    public FeedSyncTask create(FeedConfig feedConfig, ReentrantLock writeLock) {
        var reader = new UrlFeedReader(feedConfig);
        var processor = new FeedFilterProcessor(feedConfig);
        var writer = new FileFeedWriter(feedConfig, writeLock, writerBatchSize, eventPublisher);

        return new FeedSyncTask(feedConfig, reader, processor, writer);
    }
}
