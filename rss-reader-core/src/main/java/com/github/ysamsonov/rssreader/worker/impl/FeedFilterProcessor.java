package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.worker.FeedProcessor;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Filter items by {@link SyndFeed#getPublishedDate()}
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class FeedFilterProcessor implements FeedProcessor<SyndFeed, List<SyndEntry>> {

    private final FeedConfig feedConfig;

    public FeedFilterProcessor(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
    }

    @Override
    public List<SyndEntry> process(SyndFeed feed) {
        if (feed == null) {
            return Collections.emptyList();
        }

        var lastFetchDate = feedConfig.getLastFetchDate();
        return feed.getEntries()
            .stream()
            .filter(e -> Objects.nonNull(e.getPublishedDate()))
            .filter(e -> e.getPublishedDate().compareTo(lastFetchDate) > 0)
            .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
            .limit(feedConfig.getFetchCount())
            .collect(Collectors.toList())
            ;
    }
}
