package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;

import java.util.Scanner;

/**
 * Handle switching state of feed by it's identifier
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class SwitchStateFeedAction extends BaseConsoleAction {

    private final ConfigurationManager configurationManager;

    public SwitchStateFeedAction(Scanner scanner, ConfigurationManager configurationManager) {
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

        configurationManager.switchStateFeed(feedNum);
    }
}
