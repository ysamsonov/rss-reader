package com.github.ysamsonov.rssreader.exception;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-23
 */
public class TypeConvertException extends RssReaderException {

    public TypeConvertException(String message, Object... args) {
        super(message, args);
    }
}
