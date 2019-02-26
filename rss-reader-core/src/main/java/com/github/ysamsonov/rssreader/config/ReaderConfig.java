package com.github.ysamsonov.rssreader.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Getter
@Setter
public class ReaderConfig {

    private List<FeedConfig> feeds = new ArrayList<>();

    private int fetchCount = 10;

    private String fetchTime = "10m";

    public ReaderConfig addFeed(FeedConfig feed) {
        this.feeds.add(feed);
        return this;
    }

    public void deleteFeed(int feedNum) {
        this.feeds.remove(feedNum);
    }
}
