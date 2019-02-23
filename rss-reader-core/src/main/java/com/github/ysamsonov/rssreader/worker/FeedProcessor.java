package com.github.ysamsonov.rssreader.worker;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public interface FeedProcessor<SOURCE, RESULT> {
    RESULT process(SOURCE feed);
}
