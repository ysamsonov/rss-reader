package com.github.ysamsonov.rssreader.worker;

/**
 * Interface for item transformation.
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public interface FeedProcessor<I, O> {

    /**
     * Process the provided item
     *
     * @param item - to be processed
     * @return potentially modified or new item for continued processing, null if processing of the provided item should not continue.
     */
    O process(I item);
}
