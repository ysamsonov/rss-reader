package com.github.ysamsonov.rssreader.helpers;

import com.github.ysamsonov.rssreader.utils.MapBuilder;
import com.rometools.rome.feed.synd.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public final class FieldExtractors {

    public static final Map<String, Function<SyndFeed, String>> feedExt = MapBuilder.<String, Function<SyndFeed, String>>create()
        .put("copyright", SyndFeed::getCopyright)
        .put("generator", SyndFeed::getGenerator)
        .put("image", f -> f.getImage() != null ? f.getImage().getUrl() : null)
        .put("icon", f -> f.getIcon() != null ? f.getIcon().getUrl() : null)
        .buildImmutable();

    public static final Map<String, Function<SyndEntry, String>> entryExt = MapBuilder.<String, Function<SyndEntry, String>>create()
        .put("uri", SyndEntry::getUri)
        .put("title", SyndEntry::getTitle)
        .put("link", SyndEntry::getLink) //links
        .put("description", e -> e.getDescription() != null ? e.getDescription().getValue() : null)
        .put(
            "contents",
            $ -> $.getContents().stream().map(SyndContent::getValue).collect(Collectors.joining("; "))
        )
        .put(
            "enclosures",
            $ -> $.getEnclosures().stream().map(SyndEnclosure::getUrl).collect(Collectors.joining("; "))
        )
        .put("publishedDate", s -> s.getPublishedDate() != null ? s.getPublishedDate().toString() : null)
        .put("updatedDate", s -> s.getUpdatedDate() != null ? s.getUpdatedDate().toString() : null)
        .put("author", SyndEntry::getAuthor) // authors
        .put(
            "contributors",
            $ -> $.getContributors().stream().map(SyndPerson::getName).collect(Collectors.joining("; "))
        )
        .put(
            "categories",
            $ -> $.getCategories().stream().map(SyndCategory::getName).collect(Collectors.joining("; "))
        )
        .put("comments", SyndEntry::getComments)
        .buildImmutable();
}
