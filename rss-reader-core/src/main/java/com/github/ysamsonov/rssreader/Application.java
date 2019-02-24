package com.github.ysamsonov.rssreader;

import com.github.ysamsonov.rssreader.cli.CliInterface;
import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.helpers.PropertyResolver;
import com.github.ysamsonov.rssreader.worker.FeedSynchronizer;
import lombok.Getter;

import java.io.File;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-22
 */
public class Application {

    @Getter
    private static Application appInstance;

    private final ConfigurationManager configurationManager;

    private final PropertyResolver propertyResolver;

    private final FeedSynchronizer feedSynchronizer;

    private final CliInterface cliInterface;

    private Application() {
        this.propertyResolver = new PropertyResolver();
        this.configurationManager = new ConfigurationManager(getReaderConfigFile());
        this.feedSynchronizer = new FeedSynchronizer(getThreadCount());
        this.cliInterface = new CliInterface(configurationManager, this::exit);
    }

    public static void main(String[] args) {
        appInstance = new Application();
        appInstance
            .init()
            .run();
    }

    private Application init() {
        feedSynchronizer.update(configurationManager.getConfig());
        return this;
    }

    private void run() {
        cliInterface.show();
    }

    private void exit() {
        // TODO: terminate correctly
        System.exit(0);
    }

    private File getReaderConfigFile() {
        String fileName = propertyResolver.getProperty("rssreader.config.location").orElse("reader-config.json");
        return new File(fileName);
    }

    private int getThreadCount() {
        return propertyResolver.getProperty("rssreader.feed.synchronizer.pool.size", Integer.class).orElse(1);
    }
}
