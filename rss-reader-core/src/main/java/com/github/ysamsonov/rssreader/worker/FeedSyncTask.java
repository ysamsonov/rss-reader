package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Determine the complete stage of processing one feed.
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
@RequiredArgsConstructor
public class FeedSyncTask implements Runnable {

    /**
     * Feed configuration for concrete task
     */
    private final FeedConfig feedConfig;

    /**
     * Feed reader from abstract producer
     */
    private final FeedReader reader;

    /**
     * Feed processor may include filtering, converting, etc.
     */
    private final FeedProcessor<SyndFeed, SyndFeed> processor;

    /**
     * Feed writer to the abstract consumer
     */
    private final FeedWriter writer;

    /**
     * Run feed processing
     */
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
