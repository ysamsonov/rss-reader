package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;

import java.util.*;
import java.util.function.Function;

/**
 * Handle editing of feed by it's identifier.
 * During edit user can modify:
 * 1. {@link FeedConfig#getFileName()} to write
 * 2. {@link FeedConfig#isEnabled()} flag
 * 3. {@link FeedConfig#getFields()} filter
 * 4. {@link FeedConfig#getFetchTime()}
 * 5. {@link FeedConfig#getFetchCount()}
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class EditFeedAction extends BaseConsoleAction {

    private final ConfigurationManager configurationManager;

    public EditFeedAction(Scanner scanner, ConfigurationManager configurationManager) {
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

        FeedConfig originalFeedConfig = configurationManager.getConfig().getFeeds().get(feedNum);

        String fileName = read(
            String.format("Filename: (current: %s)", originalFeedConfig.getFileName()),
            Function.identity(),
            f -> true,
            originalFeedConfig.getFileName()
        );

        boolean enabled = read(
            String.format("Enabled (y/n)? (current: %s)", originalFeedConfig.isEnabled() ? "yes" : "no"),
            Parsers.booleanVal(),
            f -> true,
            originalFeedConfig.isEnabled()
        );

        Collection<String> fields = read(
            String.format(
                "Fields to write: (current: %s)\nAllowed: %s",
                originalFeedConfig.getFields().isEmpty()
                    ? "all fields"
                    : String.join(", ", originalFeedConfig.getFields()),
                String.join(", ", FieldExtractors.entryExt.keySet())
            ),
            f -> new HashSet<>(Arrays.asList(f.split(" "))),
            Validators.fields(),
            Collections.emptyList()
        );

        String fetchTime = read(
            String.format(
                "Fetch time: (current: %s)",
                originalFeedConfig.getFetchTime() != null ? originalFeedConfig.getFetchTime() : ""
            ),
            Function.identity(),
            Validators.fetchTime(),
            originalFeedConfig.getFetchTime()
        );

        FeedConfig feedConfig = new FeedConfig()
            .setUrl(originalFeedConfig.getUrl())
            .setFileName(fileName)
            .setEnabled(enabled)
            .setFields(fields)
            .setFetchTime(fetchTime);

        configurationManager.updateFeed(feedNum, feedConfig);
    }
}
