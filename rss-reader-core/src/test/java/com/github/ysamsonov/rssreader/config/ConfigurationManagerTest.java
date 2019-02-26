package com.github.ysamsonov.rssreader.config;

import com.github.ysamsonov.rssreader.event.ApplicationEventPublisher;
import com.github.ysamsonov.rssreader.junit.TempFileExtension;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class ConfigurationManagerTest {

    @RegisterExtension
    final TempFileExtension tempFileExtension = new TempFileExtension();

    @Test
    void loadConfigTstFromFile() throws IOException {
        try (var in = ConfigurationManagerTest.class.getClassLoader().getResourceAsStream("reader-config.json")) {
            var content = Objects.requireNonNull(in).readAllBytes();
            Files.write(tempFileExtension.getTmpFile().toPath(), content);
        }

        var cm = new ConfigurationManager(
            tempFileExtension.getTmpFile(),
            Mockito.mock(ApplicationEventPublisher.class)
        );

        ReaderConfig config = cm.getConfig();

        assertThat(config)
            .extracting(ReaderConfig::getFetchTime, ReaderConfig::getFetchCount)
            .contains("1h", 100);

        assertThat(config.getFeeds())
            .extracting(
                FeedConfig::getUrl,
                FeedConfig::isEnabled,
                FeedConfig::getFileName,
                FeedConfig::getFetchTime,
                FeedConfig::getFields
            )
            .contains(
                Tuple.tuple(
                    "https://news.yandex.ru/sport.rss", true, "news.txt", "10s", Arrays.asList("title", "description")
                ),
                Tuple.tuple("https://news.yandex.ru/auto.rss", false, "auto.txt", "10m", Collections.emptyList()),
                Tuple.tuple("https://news.yandex.ru/science.rss", false, "news.txt", "1d", Collections.emptyList())
            );
    }

    @Test
    void loadConfigTstFromNonExistingFile() {
        var cm = new ConfigurationManager(
            new File("unknown-file.txt"),
            Mockito.mock(ApplicationEventPublisher.class)
        );

        ReaderConfig config = cm.getConfig();
        assertThat(config)
            .extracting(ReaderConfig::getFetchTime, ReaderConfig::getFetchCount, f -> f.getFeeds().size())
            .contains("10m", 10, 0);
    }
}