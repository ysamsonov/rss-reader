package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;
import com.github.ysamsonov.rssreader.worker.DelayParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
     * 3. TODO: validate items and pubDate
     *
     * @param cm - configuration manager to get access to feed config
     * @return validator predicate
     */
    static Predicate<String> link(ConfigurationManager cm) {
        return link -> {
            try {
                boolean hasFeed = cm
                    .getConfig()
                    .getFeeds()
                    .stream()
                    .anyMatch(f -> Objects.equals(f.getUrl(), link));

                if (hasFeed) {
                    System.out.println(String.format("Feed with link '%s' already exists", link));
                    return false;
                }

                var url = new URL(link);
                var conn = url.openConnection();
                conn.connect();
                return true;
            }
            catch (MalformedURLException e) {
                System.out.println(String.format("Incorrect url %s", e.getMessage()));
                return false;
            }
            catch (IOException e) {
                System.out.println("Unable to connect to host");
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
