package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;

import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Handle creating of new feed
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class AddFeedAction extends BaseConsoleAction {

    private final ConfigurationManager configurationManager;

    public AddFeedAction(Scanner scanner, ConfigurationManager configurationManager) {
        super(scanner);
        this.configurationManager = configurationManager;
    }

    @Override
    public void exec() {
        String link = read(
            "Feed link:",
            Function.identity(),
            Validators.link(configurationManager)
        );

        String fallbackFileName = fallbackFileName(link);
        String fileName = read(
            String.format("Filename: ('%s' by default)", fallbackFileName),
            Function.identity(),
            f -> true,
            fallbackFileName
        );

        boolean enabled = read(
            "Enabled (y/n)? (yes by default)",
            Parsers.booleanVal(),
            f -> true,
            true
        );

        Collection<String> fields = read(
            String.format("Fields to write: (allowed: %s)", String.join(", ", FieldExtractors.entryExt.keySet())),
            Parsers.fields(),
            Validators.fields(),
            Collections.emptyList()
        );

        String fetchTime = read(
            String.format("Fetch time: (%s by default)", configurationManager.getConfig().getFetchTime()),
            Function.identity(),
            Validators.fetchTime(),
            configurationManager.getConfig().getFetchTime()
        );

        FeedConfig feedConfig = new FeedConfig()
            .setUrl(link)
            .setFileName(fileName)
            .setEnabled(enabled)
            .setFields(fields)
            .setFetchTime(fetchTime);

        configurationManager.addFeed(feedConfig);
        System.out.println("Feed successfully added!");
    }

    private String fallbackFileName(String link) {
        return link
            .replace(':', '_')
            .replace('%', '_')
            .replace('\\', '_')
            .replace('/', '_')
            .replace('+', '_')
            .replace('!', '_')
            ;
    }
}
