package com.github.ysamsonov.rssreader;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.helpers.PropertyResolver;
import com.github.ysamsonov.rssreader.worker.FeedSynchronizer;

import java.io.File;
import java.util.Scanner;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-22
 */
public class Application {

    private final ConfigurationManager configurationManager;

    private final PropertyResolver propertyResolver;

    private final FeedSynchronizer feedSynchronizer;

    private Application() {
        this.propertyResolver = new PropertyResolver();
        this.configurationManager = new ConfigurationManager(getReaderConfigFile());
        this.feedSynchronizer = new FeedSynchronizer();
    }

    public static void main(String[] args) {
        new Application()
            .init()
            .run();
    }

    private Application init() {
        feedSynchronizer.update(configurationManager.getConfig());
        return this;
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("await");
        scanner.nextLine();
        // TODO: terminate correctly
        System.exit(0);
    }

    private File getReaderConfigFile() {
        String fileName = propertyResolver.getProperty("rssreader.config.location").orElse("reader-config.json");
        return new File(fileName);
    }
}
