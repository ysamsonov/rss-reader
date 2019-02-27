package com.github.ysamsonov.rssreader.exception;

/**
 * Represent errors during type conversion
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class TypeConvertException extends RssReaderException {

    public TypeConvertException(String message, Object... args) {
        super(message, args);
    }
}
