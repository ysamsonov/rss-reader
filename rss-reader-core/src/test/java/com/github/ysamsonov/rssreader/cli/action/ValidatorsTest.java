package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.config.ConfigurationManager;
import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-26
 */
class ValidatorsTest {

    @Test
    void feedNumber() {
        var cm = Mockito.mock(ConfigurationManager.class);
        var rconf = Mockito.mock(ReaderConfig.class);
        var feeds = Mockito.mock(List.class);

        Mockito.when(cm.getConfig()).thenReturn(rconf);
        //noinspection unchecked
        Mockito.when(rconf.getFeeds()).thenReturn(feeds);
        Mockito.when(feeds.size()).thenReturn(10);

        // assert
        assertThat(Validators.feedNumber(cm).test(10)).isFalse();
        assertThat(Validators.feedNumber(cm).test(-10)).isFalse();
        assertThat(Validators.feedNumber(cm).test(0)).isTrue();
        assertThat(Validators.feedNumber(cm).test(5)).isTrue();
    }

    @Test
    void link() {
        var cm = Mockito.mock(ConfigurationManager.class);
        var rconf = Mockito.mock(ReaderConfig.class);
        var feeds = Arrays.asList(
            new FeedConfig().setUrl("https://news.yandex.ru/sport.rss"),
            new FeedConfig().setUrl("https://news.yandex.ru/science.rss")
        );

        Mockito.when(cm.getConfig()).thenReturn(rconf);
        Mockito.when(rconf.getFeeds()).thenReturn(feeds);

        // assert
        assertThat(Validators.link(cm).test("hpt://news.com")).isFalse();
        assertThat(Validators.link(cm).test("https://news.yandex.ru/sport.rss")).isFalse();
        assertThat(Validators.link(cm).test("https://news.yandex.ru/auto.rss")).isTrue();
        assertThat(Validators.link(cm).test("https://ne.yandex.ru/auto.rss")).isFalse();
    }

    @Test
    void fields() {
        assertThat(Validators.fields().test(Collections.emptyList())).isTrue();
        assertThat(Validators.fields().test(Arrays.asList("title", "description"))).isTrue();
        assertThat(Validators.fields().test(Arrays.asList("title", "unkField"))).isFalse();
    }

    @Test
    void fetchTime() {
        assertThat(Validators.fetchTime().test("10m")).isTrue();
        assertThat(Validators.fetchTime().test("10dd")).isFalse();
    }
}