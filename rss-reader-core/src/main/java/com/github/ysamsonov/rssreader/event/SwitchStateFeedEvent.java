package com.github.ysamsonov.rssreader.event;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Event represent change state of feed action (switch {@link FeedConfig#isEnabled()} flag)
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@Getter
public class SwitchStateFeedEvent implements ApplicationEventPublisherImpl.ApplicationEvent {

    private final FeedConfig feedConfig;

    private final boolean enabled;

    private final ReaderConfig readerConfig;

    private final Set<String> fileNames;

    public SwitchStateFeedEvent(FeedConfig feedConfig, boolean enabled, ReaderConfig readerConfig) {
        this.feedConfig = feedConfig;
        this.enabled = enabled;
        this.readerConfig = readerConfig;
        this.fileNames = readerConfig.getFeeds()
            .stream()
            .filter(f -> !f.getUrl().equals(feedConfig.getUrl()))
            .map(FeedConfig::getFileName)
            .collect(Collectors.toSet());
    }
}
