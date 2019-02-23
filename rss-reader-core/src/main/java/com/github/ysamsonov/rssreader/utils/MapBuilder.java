package com.github.ysamsonov.rssreader.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class MapBuilder<K, V> {

    private final Map<K, V> map;

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> create() {
        return createHashMap();
    }

    public static <K, V> MapBuilder<K, V> createHashMap() {
        return new MapBuilder<>(new HashMap<>());
    }

    public static <K, V> MapBuilder<K, V> createLinkedHashMap() {
        return new MapBuilder<>(new LinkedHashMap<>());
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    public Map<K, V> build() {
        return map;
    }

    public Map<K, V> buildImmutable() {
        return Collections.unmodifiableMap(map);
    }
}

