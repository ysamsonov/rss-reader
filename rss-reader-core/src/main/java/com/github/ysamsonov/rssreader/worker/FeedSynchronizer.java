package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class FeedSynchronizer {

    private final ScheduledExecutorService executorService;

    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    // закидываем кучу врайтеров, каждый принадлежит какому-то файлу и потом раздаем во внутрь
//    private final Map<String, filewriter>

    public FeedSynchronizer(int poolSize) {
        this.executorService = Executors.newScheduledThreadPool(poolSize);
    }

    public synchronized void update(ReaderConfig config) {
        for (FeedConfig feed : config.getFeeds()) {
            // TODO: открыть файлы тут и желательно держать их, но просто засунуть эту инфу в процессор ?? или врайтер
            FeedSyncTask task = new FeedSyncTask(feed);

            ScheduledFuture<?> scheduledFuture = executorService.scheduleWithFixedDelay(
                task,
                10,
                10,
                TimeUnit.SECONDS
            );

            runningTasks.put(feed.getUrl(), scheduledFuture);
        }
    }
}
