package com.github.ysamsonov.rssreader.worker;

import com.rometools.rome.feed.synd.SyndFeed;

/**
 * Strategy interface for providing the data.
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public interface FeedReader {

    /**
     * Reads data
     *
     * @return the item to be processed
     */
    SyndFeed loadData();
}
