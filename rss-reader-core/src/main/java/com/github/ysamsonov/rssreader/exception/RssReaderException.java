package com.github.ysamsonov.rssreader.exception;

/**
 * Base exception for application
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class RssReaderException extends RuntimeException {

    /**
     * Constructs a new exception with given message
     *
     * @param message - detail message
     */
    public RssReaderException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with given message template and parameters
     *
     * @param message - detail message template
     * @param args    - template parameters
     */
    public RssReaderException(String message, Object... args) {
        super(String.format(message, args));
    }

    /**
     * Constructs a new exception with given message and cause
     *
     * @param message - detail message
     * @param cause   - the cause
     */
    public RssReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
