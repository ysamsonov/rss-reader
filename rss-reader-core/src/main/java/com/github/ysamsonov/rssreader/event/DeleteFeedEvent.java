package com.github.ysamsonov.rssreader.event;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Event represent delete feed action
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@Getter
public class DeleteFeedEvent implements ApplicationEventPublisherImpl.ApplicationEvent {

    private final FeedConfig feedConfig;

    private final Set<String> fileNames;

    public DeleteFeedEvent(FeedConfig feedConfig, ReaderConfig config) {
        this.feedConfig = feedConfig;
        this.fileNames = config.getFeeds()
            .stream()
            .filter(f -> !f.getUrl().equals(feedConfig.getUrl()))
            .map(FeedConfig::getFileName)
            .collect(Collectors.toSet());
    }
}
