package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.utils.MiscUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contains parsers for CLI interface
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-25
 */
final class Parsers {

    /**
     * Parse feed number and convert it to feed index in range [0, feeds count)
     *
     * @return parser function
     */
    static Function<String, Integer> feedNumber() {
        return val -> Integer.parseInt(val) - 1;
    }

    /**
     * Parse feed fields from string to collection
     *
     * @return parser function
     */
    static Function<String, Collection<String>> fields() {
        return f -> {
            if (MiscUtils.isNullOrEmpty(f) || Objects.equals(f, "all")) {
                return Collections.emptyList();
            }

            return Arrays
                .stream(f.split(" "))
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toSet());
        };
    }

    /**
     * Parse any boolean value in y/n format to {@link Boolean}
     *
     * @return parser function
     */
    static Function<String, Boolean> booleanVal() {
        return val -> {
            if (Objects.equals(val, "y") || Objects.equals(val, "yes")) {
                return true;
            }

            if (Objects.equals(val, "n") || Objects.equals(val, "no")) {
                return false;
            }

            throw new RssReaderException("Unknown value '%s'", val);
        };
    }
}
