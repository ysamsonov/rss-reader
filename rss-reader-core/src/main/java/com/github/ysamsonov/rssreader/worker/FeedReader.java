package com.github.ysamsonov.rssreader.worker;

import com.rometools.rome.feed.synd.SyndFeed;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public interface FeedReader {
    SyndFeed loadData();
}
