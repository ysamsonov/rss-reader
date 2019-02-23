package com.github.ysamsonov.rssreader.exception;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class RssReaderException extends RuntimeException {

    public RssReaderException(String message) {
        super(message);
    }

    public RssReaderException(String message, Object... args) {
        super(String.format(message, args));
    }

    public RssReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
