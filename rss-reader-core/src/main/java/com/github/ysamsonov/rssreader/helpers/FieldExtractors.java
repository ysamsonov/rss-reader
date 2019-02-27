package com.github.ysamsonov.rssreader.helpers;

import com.github.ysamsonov.rssreader.utils.MapBuilder;
import com.rometools.rome.feed.synd.*;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A set of extractors by field name from feed
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public final class FieldExtractors {

    public static final Map<String, Function<SyndFeed, String>> feedExt = MapBuilder.<String, Function<SyndFeed, String>>create()
        .put("icon", nullSafe(SyndFeed::getIcon, SyndImage::getUrl))
        .put("image", nullSafe(SyndFeed::getImage, SyndImage::getUrl))
        .put("copyright", SyndFeed::getCopyright)
        .put("generator", SyndFeed::getGenerator)
        .buildImmutable();

    public static final Map<String, Function<SyndEntry, String>> entryExt = MapBuilder.<String, Function<SyndEntry, String>>createLinkedHashMap()
        .put("title", SyndEntry::getTitle)
        .put("description", nullSafe(SyndEntry::getDescription, SyndContent::getValue))
        .put("contents", mapCollection(SyndEntry::getContents, SyndContent::getValue))
        .put("uri", SyndEntry::getUri)
        .put("link", SyndEntry::getLink) //links
        .put("enclosures", mapCollection(SyndEntry::getEnclosures, SyndEnclosure::getUrl))
        .put("publishedDate", nullSafe(SyndEntry::getPublishedDate, Date::toString))
        .put("updatedDate", nullSafe(SyndEntry::getUpdatedDate, Date::toString))
        .put("author", SyndEntry::getAuthor) // authors
        .put("contributors", mapCollection(SyndEntry::getContributors, SyndPerson::getName))
        .put("categories", mapCollection(SyndEntry::getCategories, SyndCategory::getName))
        .put("comments", SyndEntry::getComments)
        .buildImmutable();

    private static <T, R, RES> Function<T, RES> nullSafe(
        Function<T, R> left,
        Function<R, RES> right
    ) {
        return val -> {
            if (val == null) {
                return null;
            }

            R fRes = left.apply(val);
            return fRes != null ? right.apply(fRes) : null;
        };
    }

    private static <T, ET> Function<T, String> mapCollection(
        Function<T, Collection<ET>> collectionExtractor,
        Function<ET, String> mapper
    ) {
        return nullSafe(
            collectionExtractor,
            collection ->
                collection
                    .stream()
                    .map(mapper)
                    .map(v -> v != null ? v : "")
                    .collect(Collectors.joining("; "))
        );
    }
}
