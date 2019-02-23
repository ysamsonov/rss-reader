package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-23
 */
public class FeedFilterProcessor {

    private final FeedConfig feedConfig;

    public FeedFilterProcessor(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
    }

    public SyndFeed filter(SyndFeed feed) {
        // TODO: filter entries by last update date
        return feed;
    }
}
