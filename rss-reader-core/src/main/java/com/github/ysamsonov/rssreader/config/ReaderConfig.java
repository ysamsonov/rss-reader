package com.github.ysamsonov.rssreader.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration whole application
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Getter
@Setter
public class ReaderConfig {

    /**
     * All feeds for monitoring
     */
    private List<FeedConfig> feeds = new ArrayList<>();

    /**
     * Default count of items for pooling
     */
    private int fetchCount = 10;

    /**
     * Default pooling time interval
     */
    private String fetchTime = "10m";

    public ReaderConfig addFeed(FeedConfig feed) {
        this.feeds.add(feed);
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public void deleteFeed(int feedNum) {
        this.feeds.remove(feedNum);
    }
}
