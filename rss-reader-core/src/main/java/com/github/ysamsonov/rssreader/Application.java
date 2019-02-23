package com.github.ysamsonov.rssreader;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.helpers.PropertyResolver;
import com.github.ysamsonov.rssreader.worker.FeedSynchronizer;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

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

    private Application() {
        this.propertyResolver = new PropertyResolver();
        this.configurationManager = new ConfigurationManager(getReaderConfigFile());
        this.feedSynchronizer = new FeedSynchronizer(getThreadCount());
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
        Scanner scanner = new Scanner(System.in);
        printBanner();
        printMainMenu(scanner);
    }

    private void printBanner() {
        var stream = getClass().getClassLoader().getResourceAsStream("banner.txt");
        if (stream == null) {
            System.out.println("RSS Reader");
            return;
        }

        try (
            stream;
            var reader = new InputStreamReader(stream);
            var buffReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = buffReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.print("\n\n\n");
        }
        catch (IOException ignore) {
            System.out.println("RSS Reader");
        }
    }

    private void printMainMenu(Scanner scanner) {
        System.out.println("Menu");
        System.out.println("[1] View list of feeds");
        System.out.println("[2] Add new feed");
        System.out.println("[3] Edit feed");
        System.out.println("[4] Enable/Disable feed");
        System.out.println("[5] Exit");
        System.out.println("Please enter command and press enter: ");

        while (true) {
            String line = scanner.nextLine();
            switch (line.trim()) {
                case "1":
                case "l":
                    //print list
                    break;

                case "2":
                case "a":
                    // add feed
                    break;

                case "3":
                case "e":
                    // edit
                    break;

                case "4":
                    // enable/disable
                    break;

                case "5":
                case "q":
                    // TODO: terminate correctly
                    System.exit(0);

                default:
                    System.out.println("Incorrect command!");
                    break;
            }
        }
    }

    private File getReaderConfigFile() {
        String fileName = propertyResolver.getProperty("rssreader.config.location").orElse("empty-config.json");
        return new File(fileName);
    }

    private int getThreadCount() {
        return propertyResolver.getProperty("rssreader.feed.synchronizer.pool.size", Integer.class).orElse(1);
    }
}
