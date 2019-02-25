package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.exception.RssReaderException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-25
 */
final class Parsers {

    static Function<String, Collection<String>> fields() {
        return f -> {
            if (Objects.equals(f, "all")) {
                return Collections.emptyList();
            }

            return Arrays
                .stream(f.split(" "))
                .map(String::trim)
                .collect(Collectors.toSet());
        };
    }

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
