package com.github.ysamsonov.rssreader.worker;

import com.rometools.rome.feed.synd.SyndFeed;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-23
 */
public interface FeedProcessor {
    SyndFeed process(SyndFeed feed);
}
