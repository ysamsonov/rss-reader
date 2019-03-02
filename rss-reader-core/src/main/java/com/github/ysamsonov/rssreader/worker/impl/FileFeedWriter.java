package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.event.ApplicationEventPublisher;
import com.github.ysamsonov.rssreader.event.UpdateLastFetchTimeEvent;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;
import com.github.ysamsonov.rssreader.utils.Lists;
import com.github.ysamsonov.rssreader.utils.MiscUtils;
import com.github.ysamsonov.rssreader.worker.DateProvider;
import com.github.ysamsonov.rssreader.worker.FeedWriter;
import com.rometools.rome.feed.synd.SyndEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Write data to the given file
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
public class FileFeedWriter implements FeedWriter {

    private final FeedConfig feedConfig;

    private final Predicate<String> propWritePredicate;

    private final ReentrantLock lock;

    private final ApplicationEventPublisher eventPublisher;

    private final DateProvider dateProvider;

    private final int batchSize;

    public FileFeedWriter(
        FeedConfig feedConfig,
        ReentrantLock lock,
        int batchSize,
        DateProvider dateProvider,
        ApplicationEventPublisher eventPublisher
    ) {
        this.feedConfig = feedConfig;
        this.propWritePredicate = feedConfig.fieldPredicate();
        this.lock = lock;
        this.batchSize = batchSize;
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
    }

    @Override
    public void write(List<SyndEntry> entries) {
        if (MiscUtils.isNullOrEmpty(entries)) {
            log.info("Nothing to write for '{}'", feedConfig.getUrl());
            updateLastFetchDate(new Date());
            return;
        }

        writeFeed(entries);
    }

    private void writeFeed(Collection<SyndEntry> entries) {
        log.info(
            "Write {} entries feed '{}' to file '{}'",
            entries.size(), feedConfig.getUrl(), feedConfig.getFileName()
        );

        List<List<SyndEntry>> partitions = Lists.partition(Lists.asList(entries), batchSize);
        log.debug("Split data on {} partition(s) by {} elements", partitions.size(), batchSize);

        for (int i = 0, partitionsSize = partitions.size(); i < partitionsSize; i++) {
            List<SyndEntry> partition = partitions.get(i);
            log.debug("Write partition {} of {}", i + 1, partitionsSize);

            try {
                lock.lock();
                writePartition(partition);
            }
            finally {
                lock.unlock();
            }
        }

        updateLastFetchDate(dateProvider.getDate());
    }

    private void writePartition(Collection<SyndEntry> entries) {
        try (
            FileWriter fileWriter = new FileWriter(feedConfig.getFileName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter writer = new PrintWriter(bufferedWriter)
        ) {
            for (SyndEntry entry : entries) {
                writeEntry(writer, entry);
            }
        }
        catch (Exception e) {
            var msg = String.format(
                "Error during write '%s' feed info to '%s'. %s",
                feedConfig.getUrl(), feedConfig.getFileName(), e.getMessage()
            );

            log.error(msg);
            log.debug(msg, e);
            throw new RssReaderException(msg, e);
        }
    }

    private void writeEntry(PrintWriter writer, SyndEntry entry) {
        for (var extractor : FieldExtractors.entryExt.entrySet()) {
            if (!propWritePredicate.test(extractor.getKey())) {
                log.debug("Skip property '{}' during writing", extractor.getKey());
                continue;
            }

            writer.write(extractor.getKey() + ": ");

            String value = extractor.getValue().apply(entry);
            if (!MiscUtils.isNullOrEmpty(value)) {
                writer.write(value.replace('\n', ' '));
            }
            writer.write("\n");
        }
        writer.write("\n");
    }

    private void updateLastFetchDate(Date lastFetchDate) {
        log.info("Update last fetch date for '{}'", feedConfig.getUrl());
        feedConfig.setLastFetchDate(lastFetchDate);
        eventPublisher.publish(new UpdateLastFetchTimeEvent(feedConfig, lastFetchDate));
    }
}
