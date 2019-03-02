package com.github.ysamsonov.rssreader.exception;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-03-02
 */
public class InterruptMenuException extends RssReaderException {
    public InterruptMenuException() {
        super("Handle interrupt action");
    }
}
