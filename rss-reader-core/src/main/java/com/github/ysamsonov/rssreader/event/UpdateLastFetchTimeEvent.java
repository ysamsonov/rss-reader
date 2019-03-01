package com.github.ysamsonov.rssreader.event;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-27
 */
@Getter
@RequiredArgsConstructor
public class UpdateLastFetchTimeEvent implements ApplicationEventPublisher.ApplicationEvent {
    private final FeedConfig feedConfig;
    private final Date lastFetchDate;
}
