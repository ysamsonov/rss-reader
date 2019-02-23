package com.github.ysamsonov.rssreader.helpers;

import com.github.ysamsonov.rssreader.utils.TypeConverter;

import java.util.Optional;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public final class PropertyResolver {

    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(System.getProperty(key));
    }

    public <T> Optional<T> getProperty(String key, Class<T> clazz) {
        Optional<String> property = getProperty(key);
        if (property.isEmpty()) {
            return Optional.empty();
        }

        T convertedValue = TypeConverter.convert(clazz, property.get());
        return Optional.ofNullable(convertedValue);
    }
}
