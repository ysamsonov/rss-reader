package com.github.ysamsonov.rssreader.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ysamsonov.rssreader.event.*;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.mapper.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class ConfigurationManager {

    private final Object monitor = new Object();

    private final File configFile;

    private final ApplicationEventPublisher eventPublisher;

    private final ObjectMapper mapper;

    private ReaderConfig config;

    public ConfigurationManager(File configFile, ApplicationEventPublisher eventPublisher) {
        this.configFile = configFile;
        this.eventPublisher = eventPublisher;

        this.mapper = new ObjectMapperFactory().create();

        load();
    }

    private void load() {
        log.info("Load configuration");
        if (configFile.exists()) {
            try {
                this.config = mapper.readValue(configFile, ReaderConfig.class);
            }
            catch (IOException e) {
                String msg = String.format(
                    "Error during read configuration file '%s'. %s",
                    configFile.getPath(),
                    e.getMessage()
                );

                log.error(msg);
                log.error(msg, e);
                throw new RssReaderException(msg, e);
            }
        }
        else {
            this.config = new ReaderConfig();
        }
    }

    public void addFeed(FeedConfig feed) {
        log.info("Add feed '{}'", feed.getUrl());
        eventPublisher.publish(new CreateFeedEvent(feed, config));

        config.addFeed(feed);
        persist();
    }

    public void updateFeed(int feedNum, FeedConfig feed) {
        log.info("Update feed '{}'", feed.getUrl());

        // need to update last fetch date before run new task
        feed.setLastFetchDate(config.getFeeds().get(feedNum).getLastFetchDate());

        eventPublisher.publish(new EditFeedEvent(feed, config));

        config.getFeeds().set(feedNum, feed);
        persist();
    }

    public void deleteFeed(int feedNum) {
        FeedConfig feed = config.getFeeds().get(feedNum);

        log.info("Update feed '{}'", feed.getUrl());
        eventPublisher.publish(new DeleteFeedEvent(feed));

        config.deleteFeed(feedNum);
        persist();
    }

    public void switchStateFeed(int feedNum) {
        FeedConfig feed = config.getFeeds().get(feedNum);

        log.info("Switch state feed '{}'", feed.getUrl());
        eventPublisher.publish(new SwitchStateFeedEvent(feed, !feed.isEnabled(), config));

        config.getFeeds().get(feedNum).invertState();
        persist();
    }

    public ReaderConfig getConfig() {
        return config;
    }

    public void onShutdown() {
        log.info("Persist configuration on shutdown");
        persist();
    }

    // TODO: надо переодически сбрасывать в файлы
    private void persist() {
        synchronized (monitor) {
            try {
                mapper.writeValue(configFile, config);
            }
            catch (IOException e) {
                String msg = String.format(
                    "Error during persist configuration to file '%s'. %s",
                    configFile.getPath(),
                    e.getMessage()
                );

                log.error(msg);
                log.error(msg, e);
                throw new RssReaderException(msg, e);
            }
        }
    }
}
