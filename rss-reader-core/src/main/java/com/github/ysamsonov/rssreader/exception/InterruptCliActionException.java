package com.github.ysamsonov.rssreader.exception;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-03-01
 */
public class InterruptCliActionException extends RssReaderException {

    public InterruptCliActionException() {
        super("Interrupt operation on typing 'exit'");
    }
}
