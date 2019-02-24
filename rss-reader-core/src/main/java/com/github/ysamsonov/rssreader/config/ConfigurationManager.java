package com.github.ysamsonov.rssreader.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class ConfigurationManager {

    private final ObjectMapper mapper = new ObjectMapper()
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        .enable(JsonParser.Feature.ALLOW_COMMENTS)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final File configFile;

    @Getter
    private ReaderConfig config;

    public ConfigurationManager(File configFile) {
        this.configFile = configFile;

        load();
    }

    private void load() {
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
        config.addFeed(feed);
        // TODO: call refresh
        // TODO: call persiste
    }

    public void updateFeed(int feedNum, FeedConfig feedConfig) {
        config.getFeeds().set(feedNum, feedConfig);
        // TODO: call refresh
        // TODO: call persiste
    }

    public void deleteFeed(int feedNum) {
        config.deleteFeed(feedNum);
        // TODO: call refresh
        // TODO: call persiste
    }

    public void switchStateFeed(int feedNum) {
        config.getFeeds().get(feedNum).invertState();
        // TODO: call refresh
        // TODO: call persiste
    }

    public synchronized void persist() {
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
