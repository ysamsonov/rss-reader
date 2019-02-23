package com.github.ysamsonov.rssreader.helpers;

import java.util.Optional;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class PropertyResolver {

    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(System.getProperty(key));
    }
}
