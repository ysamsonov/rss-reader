package com.github.ysamsonov.rssreader.worker;

import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Collection;

/**
 * Basic interface for output feed.
 * Class implementing this interface will be responsible for serializing objects as necessary.
 * Generally, it is responsibility of implementing class to decide which technology to use for mapping and how it should be configured.
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public interface FeedWriter {

    /**
     * Process the supplied data element
     *
     * @param entries - items to write
     */
    void write(Collection<SyndEntry> entries);
}
