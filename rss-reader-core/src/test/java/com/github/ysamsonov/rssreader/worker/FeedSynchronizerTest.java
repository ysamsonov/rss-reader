package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import com.github.ysamsonov.rssreader.event.ApplicationEventPublisher;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import com.github.ysamsonov.rssreader.junit.TempFileExtension;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class FeedSynchronizerTest {

    private static final String URL_1 = "http://mock.url/tst1.rss";

    private static final String URL_2 = "http://mock2.url/tst2.rss";

    @RegisterExtension
    final TempFileExtension resFile1 = new TempFileExtension();

    @RegisterExtension
    final TempFileExtension resFile2 = new TempFileExtension();

    private final ApplicationEventPublisher eventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private FeedSynchronizer feedSynchronizer;

    @BeforeEach
    void setUp() {
        feedSynchronizer = new FeedSynchronizer(
            4,
            (fc, wl) -> new FeedSyncTask(
                fc,
                new MockReader(fc),
                new FeedFilterProcessor(fc),
                new FileFeedWriter(fc, wl, eventPublisher)
            )
        );
    }

    @AfterEach
    void tearDown() {
        feedSynchronizer.onShutdown();
    }

    @Test
    void processIntoCommonFile() throws IOException {
        var conf = new ReaderConfig()
            .addFeed(new FeedConfig().setUrl(URL_1).setFileName(resFile1.getTmpFile().getAbsolutePath()))
            .addFeed(new FeedConfig().setUrl(URL_2).setFileName(resFile1.getTmpFile().getAbsolutePath()));

        feedSynchronizer.onStart(conf);
        awaitResult();

        String content = Files.readString(resFile1.getTmpFile().toPath());

        assertRssResult(content);
        assertAtomResult(content);
    }

    @Test
    void processInSeparateFiles() throws IOException {
        var conf = new ReaderConfig()
            .addFeed(new FeedConfig().setUrl(URL_1).setFileName(resFile1.getTmpFile().getAbsolutePath()))
            .addFeed(new FeedConfig().setUrl(URL_2).setFileName(resFile2.getTmpFile().getAbsolutePath()));

        feedSynchronizer.onStart(conf);
        awaitResult();

        String content1 = Files.readString(resFile1.getTmpFile().toPath());
        String content2 = Files.readString(resFile2.getTmpFile().toPath());

        assertRssResult(content1);
        assertAtomResult(content2);
    }

    private void assertRssResult(String content) {
        assertThat(content)
            .contains("title: Роджерс назначен главным тренером")
            .contains("description: «Лестер» объявил о назначении")

            .contains("title: «Спартак» обыграл СКА")
            .contains("description: Хоккеисты петербургского СКА")
        ;
    }

    private void assertAtomResult(String content) {
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

    @RequiredArgsConstructor
    private static class MockReader implements FeedReader {

        private final FeedConfig feedConfig;

        @Override
        public SyndFeed loadData() {
            switch (feedConfig.getUrl()) {
                case URL_1:
                    return readFeed("rss.xml");

                case URL_2:
                    return readFeed("atom.xml");

                default:
                    throw new RssReaderException("Unknown url '%s'", feedConfig.getUrl());
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