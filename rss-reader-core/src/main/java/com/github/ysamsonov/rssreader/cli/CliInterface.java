package com.github.ysamsonov.rssreader.cli;

import com.github.ysamsonov.rssreader.cli.action.AddFeedAction;
import com.github.ysamsonov.rssreader.cli.action.DeleteFeedAction;
import com.github.ysamsonov.rssreader.cli.action.EditFeedAction;
import com.github.ysamsonov.rssreader.cli.action.SwitchStateFeedAction;
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

    private final Menu mainMenu;

    public CliInterface(
        ConfigurationManager configurationManager,
        Command.Action exitAction
    ) {
        this.configurationManager = configurationManager;

        var scanner = new Scanner(System.in);
        this.mainMenu = new Menu(
            scanner,
            "Menu",
            Arrays.asList(
                new Command("View list of feeds", this::feedList),
                new Command("Add new feed", new AddFeedAction(scanner, configurationManager)),
                new Command("Edit feed", new EditFeedAction(scanner, configurationManager)),
                new Command("Enable/Disable feed", new SwitchStateFeedAction(scanner, configurationManager)),
                new Command("Delete feed", new DeleteFeedAction(scanner, configurationManager)),
                new Command("Exit", exitAction)
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
            System.out.println(String.format(
                "\tFetch time: %s",
                feed.getFetchTime() != null ? feed.getFetchTime() : "default"
            ));
            System.out.println(String.format(
                "\tEnabled: %s",
                feed.isEnabled() ? "yes" : "no"
            ));
            System.out.print("\n");
        }
    }
}
