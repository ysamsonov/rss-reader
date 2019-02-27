package com.github.ysamsonov.rssreader.event;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.config.ReaderConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event represent change state of feed action (switch {@link FeedConfig#isEnabled()} flag)
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@Getter
@RequiredArgsConstructor
public class SwitchStateFeedEvent implements ApplicationEventPublisherImpl.ApplicationEvent {
    private final FeedConfig feedConfig;
    private final boolean enabled;
    private final ReaderConfig readerConfig;
}
