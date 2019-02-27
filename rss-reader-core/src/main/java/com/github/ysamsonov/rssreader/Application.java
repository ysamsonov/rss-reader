package com.github.ysamsonov.rssreader;

import com.github.ysamsonov.rssreader.cli.CliInterface;
import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.event.*;
import com.github.ysamsonov.rssreader.helpers.PropertyResolver;
import com.github.ysamsonov.rssreader.worker.BaseFeedSyncTaskFactory;
import com.github.ysamsonov.rssreader.worker.FeedSynchronizer;
import lombok.Getter;

import java.io.File;

/**
 * Entry point for application
 *
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

    /**
     * Constructor instantiate application context
     */
    private Application() {
        this.eventPublisher = new ApplicationEventPublisherImpl();
        this.propertyResolver = new PropertyResolver();
        this.configurationManager = new ConfigurationManager(getReaderConfigFile(), eventPublisher);
        this.feedSynchronizer = new FeedSynchronizer(getSyncPoolSize(), new BaseFeedSyncTaskFactory());
        this.cliInterface = new CliInterface(configurationManager, this::exit);
    }

    /**
     * Entry point
     */
    public static void main(String[] args) {
        appInstance = new Application();
        appInstance
            .init()
            .run();
    }

    /**
     * Initialize of application context
     */
    private Application init() {
        eventPublisher.subscribe(CreateFeedEvent.class, feedSynchronizer::onCreateFeed);
        eventPublisher.subscribe(EditFeedEvent.class, feedSynchronizer::onEditFeed);
        eventPublisher.subscribe(DeleteFeedEvent.class, feedSynchronizer::onDeleteFeed);
        eventPublisher.subscribe(SwitchStateFeedEvent.class, feedSynchronizer::onSwitchStateFeed);

        feedSynchronizer.onStart(configurationManager.getConfig());
        return this;
    }

    /**
     * Run application: show interface, etc.
     */
    private void run() {
        cliInterface.show();
    }

    /**
     * Correctly shutdown application
     */
    private void exit() {
        System.out.println("Start shutdown");

        feedSynchronizer.onShutdown();
        configurationManager.onShutdown();

        System.out.println("Shutdown");
        System.exit(0);
    }

    /**
     * Find file of configuration location {@link ConfigurationManager}
     *
     * @return user given file or default
     */
    private File getReaderConfigFile() {
        String fileName = propertyResolver.getProperty("rssreader.config.location").orElse("reader-config.json");
        return new File(fileName);
    }

    /**
     * Load pool size for {@link FeedSynchronizer}
     *
     * @return user given pool size or default
     */
    private int getSyncPoolSize() {
        return propertyResolver.getProperty("rssreader.feed.synchronizer.pool.size", Integer.class).orElse(4);
    }
}
