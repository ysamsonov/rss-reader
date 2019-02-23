package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class FeedSynchronizer {

    private final ScheduledExecutorService executorService;

    public FeedSynchronizer() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void update(ReaderConfig config) {
        for (FeedConfig feed : config.getFeeds()) {
            // TODO: открыть файлы тут и желательно держать их, но просто засунуть эту инфу в процессор ?? или врайтер
            FeedSyncTask task = new FeedSyncTask(feed);

            executorService.scheduleAtFixedRate(
                task,
                10,
                10,
                TimeUnit.SECONDS
            );
        }
    }
}
