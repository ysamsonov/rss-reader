package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class FeedReader {

    private final FeedConfig feedConfig;

    private final URL url;

    @SneakyThrows
    public FeedReader(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
        this.url = new URL(feedConfig.getUrl());
    }

    @SneakyThrows
    public SyndFeed loadData() {
        log.info("Load data for feed '{}'", feedConfig.getUrl());
        var xmlReader = new XmlReader(url);
        var feedInput = new SyndFeedInput();
        var feed = feedInput.build(xmlReader);
        return feed;
    }
}
