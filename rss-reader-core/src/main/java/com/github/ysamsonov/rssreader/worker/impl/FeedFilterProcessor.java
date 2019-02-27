package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.worker.FeedProcessor;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.Date;
import java.util.Optional;

/**
 * Filter items by {@link SyndFeed#getPublishedDate()}
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class FeedFilterProcessor implements FeedProcessor<SyndFeed, SyndFeed> {

    private final FeedConfig feedConfig;

    public FeedFilterProcessor(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
    }

    @Override
    public SyndFeed process(SyndFeed feed) {
        if (feed == null) {
            return null;
        }

        Optional<Date> publishedDate = findPublishedDate(feed);
        if (publishedDate.isEmpty()) {
            return feed;
        }

        var lastFetchDate = feedConfig.getLastFetchDate();
        if (lastFetchDate == null || publishedDate.get().compareTo(lastFetchDate) >= 0) {
            return feed;
        }

        return null;
    }

    private Optional<Date> findPublishedDate(SyndFeed feed) {
        var publishedDate = feed.getPublishedDate();
        if (publishedDate != null) {
            return Optional.of(publishedDate);
        }

        for (var entry : feed.getEntries()) {
            if (entry.getPublishedDate() != null) {
                return Optional.of(entry.getPublishedDate());
            }
        }

        return Optional.empty();
    }
}
