package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.rometools.rome.feed.synd.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class FeedFilterProcessorTest {

    @Test
    void nullInputTest() {
        var processor = new FeedFilterProcessor(feedConfig());

        assertThat(processor.process(null)).isEmpty();
    }

    // skip all items if published date is empty. published date should be always not null
    @Test
    void emptyPublishedDate() {
        var processor = new FeedFilterProcessor(feedConfig());

        var feed = feed();
        feed.setEntries(
            Arrays.asList(
                entry(),
                entry(),
                entry()
            )
        );
        var processed = processor.process(feed);
        assertThat(processed).isEmpty();
    }

    @Test
    void filterByPublishedDate() {
        var feedConfig = feedConfig().setLastFetchDate(new Date(System.currentTimeMillis() - 50_000));
        var processor = new FeedFilterProcessor(feedConfig);

        var feed = feed();
        feed.setEntries(
            Arrays.asList(
                entry("en1", new Date(System.currentTimeMillis() - 20_000)),
                entry("en2", new Date(System.currentTimeMillis() - 10_000)),
                entry("en4", new Date(System.currentTimeMillis() - 70_000)),
                entry("en3", new Date(System.currentTimeMillis() - 30_000))
            )
        );

        var processed = processor.process(feed);
        assertThat(processed)
            .extracting(SyndEntry::getTitle)
            .containsExactly("en3", "en1", "en2");
    }

    private FeedConfig feedConfig() {
        return new FeedConfig()
            .setUrl("http://fake.com/rss.xml")
            .setLastFetchDate(new Date());
    }

    private SyndFeed feed() {
        return new SyndFeedImpl();
    }

    private SyndEntry entry() {
        return entry(null, null);
    }

    private SyndEntry entry(String title, Date publishedDate) {
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle(title != null ? title : "Super Title");
        SyndContentImpl description = new SyndContentImpl();
        description.setValue("Super Description");
        entry.setDescription(description);
        entry.setAuthor("Yurez");
        entry.setPublishedDate(publishedDate);
        if (publishedDate != null) {
            entry.setPublishedDate(publishedDate);
        }
        return entry;
    }
}