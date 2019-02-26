package com.github.ysamsonov.rssreader.worker.impl;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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

        assertThat(processor.process(null)).isNull();
    }

    @Test
    void emptyPublishedDate() {
        var processor = new FeedFilterProcessor(feedConfig());

        var feed = syndFeed();
        var processed = processor.process(feed);
        assertThat(processed == feed).isTrue();
    }

    @Test
    void filterByPublishedDateWithoutPrevFetch() {
        var processor = new FeedFilterProcessor(feedConfig());

        var feed = syndFeed();
        feed.setPublishedDate(new Date(System.currentTimeMillis() - 10_000));

        var processed = processor.process(feed);
        assertThat(processed == feed).isTrue();
    }

    @Test
    void filterByPublishedDateFromEntryWithoutPrevFetch() {
        var processor = new FeedFilterProcessor(feedConfig());

        var feed = syndFeed();
        feed.getEntries().get(0).setPublishedDate(new Date(System.currentTimeMillis() - 10_000));

        var processed = processor.process(feed);
        assertThat(processed == feed).isTrue();
    }

    @Test
    void filterByPublishedDateWithPrevFetch() {
        var feedConfig = feedConfig().setLastFetchDate(new Date());
        var processor = new FeedFilterProcessor(feedConfig);

        var feed = syndFeed();
        feed.setPublishedDate(new Date(System.currentTimeMillis() - 10_000));

        var processed = processor.process(feed);
        assertThat(processed).isNull();
    }

    @Test
    void filterByPublishedDateFromEntryWithPrevFetch() {
        var feedConfig = feedConfig().setLastFetchDate(new Date());
        var processor = new FeedFilterProcessor(feedConfig);

        var feed = syndFeed();
        feed.getEntries().get(0).setPublishedDate(new Date(System.currentTimeMillis() - 10_000));

        var processed = processor.process(feed);
        assertThat(processed).isNull();
    }

    private FeedConfig feedConfig() {
        return new FeedConfig()
            .setUrl("http://fake.com/rss.xml");
    }

    private SyndFeed syndFeed() {
        SyndFeedImpl syndFeed = new SyndFeedImpl();

        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle("Super Title");
        SyndContentImpl description = new SyndContentImpl();
        description.setValue("Super Description");
        entry.setDescription(description);
        entry.setAuthor("Yurez");

        syndFeed.setEntries(Collections.singletonList(entry));
        return syndFeed;
    }
}