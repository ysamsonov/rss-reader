package com.github.ysamsonov.rssreader.utils;

import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * A set of utilities methods for collection
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-03-01
 */
@UtilityClass
public final class Lists {

    /**
     * Returns consecutive  of a list, each of the same size (the final list may be smaller)
     *
     * @param list - the list to return consecutive sublists of
     * @param size - the desired size of each sublist (the last may besmaller)
     * @return a list of consecutive sublists
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        Objects.requireNonNull(list);
        if (size <= 0) {
            throw new IllegalArgumentException("Size of partitions should be more than 0.");
        }

        return new Partition<>(list, size);
    }

    public static <T> List<T> asList(Collection<T> collection) {
        if (collection == null) {
            return null;
        }

        return collection instanceof List
            ? (List<T>) collection
            : new ArrayList<>(collection);
    }

    private static class Partition<T> extends AbstractList<List<T>> {
        final List<T> list;
        final int size;

        Partition(List<T> list, int size) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get(int index) {
            int start = index * size;
            int end = Math.min(start + size, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {
            int result = list.size() / size;
            if (result * size != list.size()) {
                result++;
            }
            return result;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }
}
