package com.github.ysamsonov.rssreader.cli;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.config.FeedConfig;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@RequiredArgsConstructor
public class AddFeedAction implements Command.Action {

    private final Scanner scanner;

    private final ConfigurationManager configurationManager;

    @Override
    public void exec() {
        String link = link();
        String fileName = fileName(link);
        boolean enabled = enabledFlag();
        Collection<String> fields = fields();
        String fetchTime = fetchTime();

        FeedConfig config = new FeedConfig()
            .setUrl(link)
            .setFileName(fileName)
            .setEnabled(enabled)
            .setFields(fields)
            .setFetchTime(fetchTime);

        System.out.println();

    }

    private String link() {
        System.out.println("Feed link:");
        String link;
        do {
            link = scanner.nextLine().trim();
        } while (!validateLink(link));
        return link;
    }

    private boolean validateLink(String link) {
        return true;
    }

    private String fileName(String link) {
        System.out.println("Filename: (%s by default)");
        String fileName = scanner.nextLine().trim();
        return fileName;
    }

    private boolean enabledFlag() {
        System.out.println("Enabled by default (y/n)? (yes by default)");
        String enabled = scanner.nextLine().trim();
        return true;
    }

    private Collection<String> fields() {
        System.out.println("Fields to write: ()");
        String[] fields = scanner.nextLine().trim().split(" ");
        return new HashSet<>(Arrays.asList(fields));
    }

    private String fetchTime() {
        System.out.println("Fetch time: (%s by default)");
        String fetchTime = scanner.nextLine().trim();
        return fetchTime;
    }
}
