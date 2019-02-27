package com.github.ysamsonov.rssreader.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ysamsonov.rssreader.event.*;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.mapper.ObjectMapperFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Manages application settings and provides CRUD methods for them
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class ConfigurationManager {

    /**
     * Lock  for write operations
     */
    private final Object monitor = new Object();

    /**
     * File with application configuration
     */
    private final File configFile;

    /**
     * Publishes events occurring with configuration
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Configuration deserializer
     */
    private final ObjectMapper mapper;

    /**
     * Cache of application config
     */
    private ReaderConfig config;

    public ConfigurationManager(File configFile, ApplicationEventPublisher eventPublisher) {
        this.configFile = configFile;
        this.eventPublisher = eventPublisher;

        this.mapper = new ObjectMapperFactory().create();

        load();
    }

    /**
     * Load configuration on application startup
     */
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

    /**
     * Add new feed to configuration
     *
     * @param feed - new feed
     */
    public void addFeed(@NonNull FeedConfig feed) {
        log.info("Add feed '{}'", feed.getUrl());
        eventPublisher.publish(new CreateFeedEvent(feed, config));

        config.addFeed(feed);
        persist();
    }

    /**
     * Update feed by given identifier
     *
     * @param feedNum - identifier of feed
     * @param feed    - updated instance
     */
    public void updateFeed(int feedNum, FeedConfig feed) {
        log.info("Update feed '{}'", feed.getUrl());

        // need to update last fetch date before run new task
        feed.setLastFetchDate(config.getFeeds().get(feedNum).getLastFetchDate());

        eventPublisher.publish(new EditFeedEvent(feed, config));

        config.getFeeds().set(feedNum, feed);
        persist();
    }

    /**
     * Delete feed by given identifier
     *
     * @param feedNum - identifier of feed
     */
    public void deleteFeed(int feedNum) {
        FeedConfig feed = config.getFeeds().get(feedNum);

        log.info("Update feed '{}'", feed.getUrl());
        eventPublisher.publish(new DeleteFeedEvent(feed));

        config.deleteFeed(feedNum);
        persist();
    }

    /**
     * Switch state of feed by given identifier
     *
     * @param feedNum - identifier of feed
     */
    public void switchStateFeed(int feedNum) {
        FeedConfig feed = config.getFeeds().get(feedNum);

        log.info("Switch state feed '{}'", feed.getUrl());
        eventPublisher.publish(new SwitchStateFeedEvent(feed, !feed.isEnabled(), config));

        config.getFeeds().get(feedNum).invertState();
        persist();
    }

    /**
     * Get config from cache
     *
     * @return configuration
     */
    public ReaderConfig getConfig() {
        return config;
    }

    /**
     * Perform operations on shutdown: persist configuration to file storage, etc.
     */
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
