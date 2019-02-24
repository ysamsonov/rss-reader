package com.github.ysamsonov.rssreader.cli;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.config.FeedConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class CliInterface {

    private final ConfigurationManager configurationManager;

    private final Scanner scanner;

    private final Menu mainMenu;

    public CliInterface(
        ConfigurationManager configurationManager
    ) {
        this.configurationManager = configurationManager;
        this.scanner = new Scanner(System.in);
        this.mainMenu = new Menu(
            scanner,
            "Menu",
            Arrays.asList(
                new Command("View list of feeds", this::feedList),
                new Command("Add new feed", new AddFeedAction(scanner, configurationManager)),
                new Command("Edit feed", new EditFeedAction(scanner)),
                new Command("Enable/Disable feed", this::switchState),
                new Command("Exit", this::exit)
            )
        );
    }

    public void show() {
        new BannerWriter().write();
        printMainMenu();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void printMainMenu() {
        while (true) {
            mainMenu.show();
        }
    }

    private void feedList() {
        List<FeedConfig> feeds = configurationManager.getConfig().getFeeds();
        if (feeds.isEmpty()) {
            System.out.println("Feed is empty.");
            return;
        }

        for (int i = 0; i < feeds.size(); i++) {
            FeedConfig feed = feeds.get(i);
            System.out.println(String.format(
                "Feed #%d",
                (i + 1))
            );
            System.out.println(String.format(
                "\tUrl: %s",
                feed.getUrl()
            ));
            System.out.println(String.format(
                "\tFile name: %s",
                feed.getFileName()
            ));
            System.out.println(String.format(
                "\tFields: %s",
                String.join("; ", feed.getFields())
            ));
            System.out.print("\n");
        }
    }

    private void switchState() {

    }

    private void exit() {
        // TODO: terminate correctly
        System.exit(0);
    }
}
