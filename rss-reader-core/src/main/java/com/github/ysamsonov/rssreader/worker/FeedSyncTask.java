package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class FeedSyncTask implements Runnable {

    private final FeedConfig feedConfig;

    private final FeedReader reader;

    private final FeedFilterProcessor processor;

    private final FeedWriter writer;

    // TODO куда писать надо принимать снаружи, а снаружи ииметь холдер стримов
    public FeedSyncTask(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;

        this.reader = new FeedReader(feedConfig);
        this.processor = new FeedFilterProcessor(feedConfig);
        this.writer = new FeedWriter(feedConfig);
    }

    @Override
    public void run() {
        log.info("Run sync for feed '{}'", feedConfig.getUrl());
        try {
            final var originalFeed = reader.loadData();
            final var filteredFeed = processor.filter(originalFeed);
            writer.write(filteredFeed);
        }
        catch (Exception e) {
            // TODO: fix it!
            e.printStackTrace();
        }
    }
}
