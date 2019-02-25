package com.github.ysamsonov.rssreader;

import com.github.ysamsonov.rssreader.cli.CliInterface;
import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.event.*;
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

    private final ApplicationEventPublisherImpl eventPublisher;

    private Application() {
        this.eventPublisher = new ApplicationEventPublisherImpl();
        this.propertyResolver = new PropertyResolver();
        this.configurationManager = new ConfigurationManager(getReaderConfigFile(), eventPublisher);
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
        eventPublisher.subscribe(CreateFeedEvent.class, feedSynchronizer::onCreateFeed);
        eventPublisher.subscribe(EditFeedEvent.class, feedSynchronizer::onEditFeed);
        eventPublisher.subscribe(DeleteFeedEvent.class, feedSynchronizer::onDeleteFeed);
        eventPublisher.subscribe(SwitchStateFeedEvent.class, feedSynchronizer::onSwitchStateFeed);

        feedSynchronizer.onStart(configurationManager.getConfig());
        return this;
    }

    private void run() {
        cliInterface.show();
    }

    private void exit() {
        System.out.println("Start shutdown");

        feedSynchronizer.onShutdown();
        configurationManager.onShutdown();

        System.out.println("Shutdown");
        System.exit(0);
    }

    private File getReaderConfigFile() {
        String fileName = propertyResolver.getProperty("rssreader.config.location").orElse("reader-config.json");
        return new File(fileName);
    }

    private int getThreadCount() {
        return propertyResolver.getProperty("rssreader.feed.synchronizer.pool.size", Integer.class).orElse(4);
    }
}
