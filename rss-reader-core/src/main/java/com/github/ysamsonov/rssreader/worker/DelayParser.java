package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.exception.RssReaderException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-23
 */
public class DelayParser {

    public Delay parse(@NonNull String delay) {
        if (delay.endsWith("ms")) {
            return Delay.of(
                parseTime(delay.substring(0, delay.length() - 2)),
                TimeUnit.MILLISECONDS
            );
        }
        else if (delay.endsWith("s")) {
            return Delay.of(
                parseTime(delay.substring(0, delay.length() - 1)),
                TimeUnit.SECONDS
            );
        }
        else if (delay.endsWith("m")) {
            return Delay.of(
                parseTime(delay.substring(0, delay.length() - 1)),
                TimeUnit.MINUTES
            );
        }
        else if (delay.endsWith("h")) {
            return Delay.of(
                parseTime(delay.substring(0, delay.length() - 1)),
                TimeUnit.HOURS
            );
        }
        else if (delay.endsWith("d")) {
            return Delay.of(
                parseTime(delay.substring(0, delay.length() - 1)),
                TimeUnit.DAYS
            );
        }

        throw new RssReaderException("Cannot find suitable unit for delay=%s", delay);
    }

    private long parseTime(String delay) {
        try {
            return Long.parseLong(delay);
        }
        catch (NumberFormatException e) {
            throw new RssReaderException("Cannot parse time from %s. %s", delay, e.getMessage());
        }
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class Delay {
        private final long delay;
        private final TimeUnit unit;
    }
}
