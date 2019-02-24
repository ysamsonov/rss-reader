package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
@RequiredArgsConstructor
public class FeedSyncTask implements Runnable {

    private final FeedConfig feedConfig;

    private final FeedReader reader;

    private final FeedProcessor<SyndFeed, SyndFeed> processor;

    private final FeedWriter writer;

    @Override
    public void run() {
        log.info("Run sync for feed '{}'", feedConfig.getUrl());
        try {
            final var originalFeed = reader.loadData();
            final var filteredFeed = processor.process(originalFeed);
            writer.write(filteredFeed);
        }
        catch (Exception e) {
            var msg = String.format(
                "Error during process feed '%s'. %s",
                feedConfig.getUrl(), e.getMessage()
            );
            log.error(msg);
            log.debug(msg, e);
        }
    }
}
