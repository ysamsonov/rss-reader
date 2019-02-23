package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.worker.FeedReader;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class UrlFeedReader implements FeedReader {

    private final FeedConfig feedConfig;

    private final URL url;

    @SneakyThrows
    public UrlFeedReader(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
        this.url = new URL(feedConfig.getUrl());
    }

    @Override
    public SyndFeed loadData() {
        log.info("Load data for feed '{}'", feedConfig.getUrl());
        try (var xmlReader = new XmlReader(url)) {
            var feedInput = new SyndFeedInput();
            return feedInput.build(xmlReader);
        }
        catch (FeedException | IOException e) {
            var msg = String.format(
                "Error during read feed '%s'. %s",
                feedConfig.getUrl(),
                e.getMessage()
            );

            log.error(msg);
            log.debug(msg, e);
            throw new RssReaderException(msg, e);
        }
    }
}
