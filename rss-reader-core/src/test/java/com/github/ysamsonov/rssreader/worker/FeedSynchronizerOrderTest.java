package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import com.github.ysamsonov.rssreader.event.ApplicationEventPublisher;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.junit.TempFileExtension;
import com.github.ysamsonov.rssreader.utils.TypeConverter;
import com.github.ysamsonov.rssreader.worker.impl.FeedFilterProcessor;
import com.github.ysamsonov.rssreader.worker.impl.FileFeedWriter;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class FeedSynchronizerOrderTest {

    private static final String URL_1 = "http://mock.url/tst1.rss";

    @RegisterExtension
    final TempFileExtension resFile1 = new TempFileExtension();

    private final ApplicationEventPublisher eventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private FeedSynchronizer feedSynchronizer;

    @BeforeEach
    void setUp() {
        feedSynchronizer = new FeedSynchronizer(
            2,
            (fc, wl) -> new FeedSyncTask(
                fc,
                new MockReader(),
                new FeedFilterProcessor(fc),
                new FileFeedWriter(fc, wl, 10, new MockDateProvider(), eventPublisher)
            )
        );
    }

    @AfterEach
    void tearDown() {
        feedSynchronizer.onShutdown();
    }

    @Test
    void processWithNewItemsAfterFirstIteration() throws IOException {
        var conf = new ReaderConfig()
            .addFeed(new FeedConfig()
                .setUrl(URL_1)
                .setFileName(resFile1.getTmpFile().getAbsolutePath())
                .setLastFetchDate(TypeConverter.convert(Date.class, "2019-02-24T21:34:36.338Z"))
                .setFetchCount(1)
                .setFetchTime("2s")
            );

        feedSynchronizer.onStart(conf);
        awaitResult();

        String content1 = Files.readString(resFile1.getTmpFile().toPath());

        assertRssResultFullWithOrder(content1);
    }

    private void assertRssResultFullWithOrder(String content) {
        // check correct order of items
        assertThat(content)
            .containsSubsequence("title: Роджерс назначен главным тренером", "title: «Спартак» обыграл СКА");

        // check contains required text
        assertThat(content)
            .contains("title: Роджерс назначен главным тренером")
            .contains("description: «Лестер» объявил о назначении")

            .contains("title: «Спартак» обыграл СКА")
            .contains("description: Хоккеисты петербургского СКА")
        ;
    }

    private void assertAtomResultOnlyFirstItems(String content) {
        // check correct order of items
        assertThat(content)
            .containsSubsequence("title: Our Cat Thinks A Dog", "title: Ziggy The Hunter");

        // check contains required text
        assertThat(content)
            .contains("title: Our Cat Thinks A Dog")
            .contains("contents: Ziggy really is quite the cat")

            .contains("title: Ziggy The Hunter")
            .contains("contents: The hunter takes down his prey.")
        ;
    }

    @SneakyThrows
    private void awaitResult() {
        Thread.sleep(2_500);
    }

    private static class MockDateProvider implements DateProvider {

        private int iterationNum = 0;

        @Override
        public Date getDate() {
            iterationNum++;
            switch (iterationNum) {
                case 1:
                    // 26 Feb 2019 19:12:00 +0000
                    return new Date(1551208320000L);

                case 2:
                    return new Date();

                default:
                    throw new RssReaderException("Unexpected iteration num '%d'", iterationNum);
            }
        }
    }

    @RequiredArgsConstructor
    private static class MockReader implements FeedReader {

        private int iterationNum = 0;

        @Override
        public SyndFeed loadData() {
            iterationNum++;
            switch (iterationNum) {
                case 1:
                    return readFeed("rss-first.xml");

                case 2:
                    return readFeed("rss-second.xml");

                default:
                    throw new RssReaderException("Unexpected iteration num '%d'", iterationNum);
            }
        }

        @SneakyThrows
        private SyndFeed readFeed(String fname) {
            try (
                var inputStream = MockReader.class.getClassLoader().getResourceAsStream(fname);
                var xmlReader = new XmlReader(inputStream)
            ) {
                var feedInput = new SyndFeedInput();
                return feedInput.build(xmlReader);
            }
        }
    }
}