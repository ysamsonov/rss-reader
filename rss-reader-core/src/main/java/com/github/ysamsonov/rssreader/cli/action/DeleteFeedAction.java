package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;

import java.util.Scanner;

/**
 * Handle deleting feed by it's identifier
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class DeleteFeedAction extends BaseConsoleAction {

    private final ConfigurationManager configurationManager;

    public DeleteFeedAction(Scanner scanner, ConfigurationManager configurationManager) {
        super(scanner);
        this.configurationManager = configurationManager;
    }

    @Override
    public void exec() {
        int feedNum = read(
            "Feed number: ",
            Parsers.feedNumber(),
            Validators.feedNumber(configurationManager)
        );

        configurationManager.deleteFeed(feedNum);
    }
}
