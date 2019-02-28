package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.event.ApplicationEventPublisher;
import com.github.ysamsonov.rssreader.junit.TempFileExtension;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class FileFeedWriterTest {

    @RegisterExtension
    final TempFileExtension tempFileExtension = new TempFileExtension();

    private final ApplicationEventPublisher eventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    @Test
    void writeNull() throws IOException {
        FileFeedWriter writer = new FileFeedWriter(
            feedConfig(),
            new ReentrantLock(),
            eventPublisher
        );

        writer.write(null);

        String content = Files.readString(tempFileExtension.getTmpFile().toPath());
        assertThat(content).isEmpty();
    }

    @Test
    void writeAllFields() throws IOException {
        FileFeedWriter writer = new FileFeedWriter(
            feedConfig(),
            new ReentrantLock(),
            eventPublisher
        );

        SyndFeed syndFeed = syndFeed();
        writer.write(syndFeed.getEntries());

        String content = Files.readString(tempFileExtension.getTmpFile().toPath());
        assertThat(content)
            .contains("title: Super Title")
            .contains("description: Super Description")
            .contains("author: Yurez");
    }

    @Test
    void writeWithFilters() throws IOException {
        FileFeedWriter writer = new FileFeedWriter(
            feedConfigWithFilters(),
            new ReentrantLock(),
            eventPublisher
        );

        SyndFeed syndFeed = syndFeed();
        writer.write(syndFeed.getEntries());

        String content = Files.readString(tempFileExtension.getTmpFile().toPath());
        assertThat(content)
            .contains("title: Super Title")
            .contains("description: Super Description")
            .doesNotContain("author: Yurez");
    }

    private FeedConfig feedConfig() {
        return new FeedConfig()
            .setUrl("http://fake.com/rss.xml")
            .setFileName(tempFileExtension.getTmpFile().getAbsolutePath());
    }

    private FeedConfig feedConfigWithFilters() {
        return feedConfig()
            .setFields(Arrays.asList("title", "description"));
    }

    private SyndFeed syndFeed() {
        SyndFeedImpl syndFeed = new SyndFeedImpl();

        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle("Super Title");
        SyndContentImpl description = new SyndContentImpl();
        description.setValue("Super Description");
        entry.setDescription(description);
        entry.setAuthor("Yurez");
        entry.setPublishedDate(new Date());

        syndFeed.setEntries(Collections.singletonList(entry));
        return syndFeed;
    }
}