package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;
import com.github.ysamsonov.rssreader.worker.FeedWriter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class FileFeedWriter implements FeedWriter {

    private final FeedConfig feedConfig;

    public FileFeedWriter(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
    }

    @Override
    @SneakyThrows
    public void write(SyndFeed syndFeed) {
        if (syndFeed == null) {
            log.info("Nothing to write for '{}'", feedConfig.getUrl());
            updateLastFetchDate();
            return;
        }

        writeFeed(syndFeed);
    }

    private void writeFeed(SyndFeed syndFeed) throws IOException {
        log.info("Write data from feed '{}' to file '{}'", feedConfig.getUrl(), feedConfig.getFileName());
        try (
            FileWriter fw = new FileWriter(feedConfig.getFileName(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)
        ) {
            try {
                for (SyndEntry entry : syndFeed.getEntries()) {
                    writeEntry(out, entry);
                }
            }
            catch (Exception e) {
                var msg = String.format(
                    "Error during write '%s' feed info to '%s'. %s",
                    feedConfig.getUrl(), feedConfig.getFileName(), e.getMessage()
                );

                log.error(msg);
                log.debug(msg, e);
                throw new RssReaderException(msg);
            }
        }

        updateLastFetchDate();
    }

    @SneakyThrows
    private void writeEntry(PrintWriter out, SyndEntry entry) {
        for (var extractor : FieldExtractors.entryExt.entrySet()) {
            out.write(extractor.getKey() + ": ");

            Object value = extractor.getValue().apply(entry);
            if (value != null) {
                out.write(value.toString());
            }
            out.write("\n");
        }
        out.write("\n");
    }

    private void updateLastFetchDate() {
        log.info("Update last fetch date for '{}'", feedConfig.getUrl());
        feedConfig.setLastFetchDate(new Date());
    }
}
