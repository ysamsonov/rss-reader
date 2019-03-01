package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;
import com.github.ysamsonov.rssreader.worker.DelayParser;
import com.github.ysamsonov.rssreader.worker.impl.UrlFeedReader;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A set of validators for CLI interface
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
final class Validators {

    /**
     * Feed number validator by feed id
     *
     * @param cm - configuration manager to get access to app config
     * @return validator predicate
     */
    static Predicate<Integer> feedNumber(ConfigurationManager cm) {
        return num -> 0 <= num && num < cm.getConfig().getFeeds().size();
    }

    /**
     * Validate feed link.
     * 1. check uniqueness for a given link
     * 2. try ping feed link
     * 3. validate items and pubDate
     *
     * @param cm - configuration manager to get access to feed config
     * @return validator predicate
     */
    static Predicate<String> link(ConfigurationManager cm) {
        return existLink(cm).and(pingLink());
    }

    private static Predicate<String> existLink(ConfigurationManager cm) {
        return link -> {
            boolean hasFeed = cm
                .getConfig()
                .getFeeds()
                .stream()
                .anyMatch(f -> Objects.equals(f.getUrl(), link));

            if (hasFeed) {
                System.out.println(String.format("Feed with link '%s' already exists", link));
                return false;
            }
            return true;
        };
    }

    private static Predicate<String> pingLink() {
        return link -> {
            try {
                var reader = new UrlFeedReader(new FeedConfig().setUrl(link));
                var feed = reader.loadData();

                if (feed.getEntries().isEmpty()) {
                    System.out.println("Feed doesn't have a history!");
                    return false;
                }

                if (feed.getEntries().iterator().next().getPublishedDate() == null) {
                    System.out.println("Feed doesn't have a published date on entries!");
                    return false;
                }

                return true;
            }
            catch (Exception e) {
                System.out.println(String.format("Error during connect to url '%s'", e.getMessage()));
                return false;
            }
        };
    }

    /**
     * Validate feed fields filter.
     *
     * @return validator predicate
     */
    static Predicate<Collection<String>> fields() {
        return fields -> {
            if (fields.isEmpty()) {
                return true;
            }

            return FieldExtractors.entryExt.keySet().containsAll(fields);
        };
    }

    /**
     * Validate feed fetch time format
     *
     * @return validator predicate
     */
    static Predicate<String> fetchTime() {
        return fTime -> {
            try {
                new DelayParser().parse(fTime);
                return true;
            }
            catch (RssReaderException e) {
                System.out.println(String.format("Incorrect fetch time. %s", e.getMessage()));
                return false;
            }
        };
    }

    /**
     * Always return true fo value validation
     *
     * @return true
     */
    static <T> Predicate<T> anyValue() {
        return v -> true;
    }
}
