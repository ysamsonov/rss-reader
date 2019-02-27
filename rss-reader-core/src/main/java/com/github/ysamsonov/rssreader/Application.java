package com.github.ysamsonov.rssreader;

import com.github.ysamsonov.rssreader.cli.CliInterface;
import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.event.*;
import com.github.ysamsonov.rssreader.helpers.PropertyResolver;
import com.github.ysamsonov.rssreader.worker.BaseFeedSyncTaskFactory;
import com.github.ysamsonov.rssreader.worker.FeedSynchronizer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Entry point for application
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-22
 */
@Slf4j
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
        this.feedSynchronizer = new FeedSynchronizer(getSyncPoolSize(), new BaseFeedSyncTaskFactory(eventPublisher));
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
        eventPublisher.subscribe(UpdateLastFetchTimeEvent.class, configurationManager::onUpdateLastFetchTime);

        feedSynchronizer.onStart(configurationManager.getConfig());

        addShutdownHook();
        return this;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook. Call onExit");
            onExit();
        }));
    }

    /**
     * Run application: show interface, etc.
     */
    private void run() {
        cliInterface.show();
    }

    /**
     * Call onExit and shutdown on user action
     */
    private void exit() {
        log.info("Exit from application manually (by user action)");
        onExit();
        System.exit(0);
    }

    /**
     * Correctly shutdown application
     */
    private void onExit() {
        System.out.println("Start shutdown");

        feedSynchronizer.onShutdown();
        configurationManager.onShutdown();

        System.out.println("Shutdown");
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
