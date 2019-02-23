package com.github.ysamsonov.rssreader.helpers;

import com.rometools.rome.feed.synd.*;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public final class FieldExtractors {

    // TODO: use map builder
    public static final HashMap<String, Function<SyndFeed, String>> feedExt = new HashMap<>();

    public static final HashMap<String, Function<SyndEntry, String>> entryExt = new HashMap<>();

    static {
        feedExt.put("copyright", SyndFeed::getCopyright);
        feedExt.put("generator", SyndFeed::getGenerator);
        feedExt.put("image", f -> f.getImage() != null ? f.getImage().getUrl() : null);
        feedExt.put("icon", f -> f.getIcon() != null ? f.getIcon().getUrl() : null);


        entryExt.put("uri", SyndEntry::getUri);
        entryExt.put("title", SyndEntry::getTitle);
        entryExt.put("link", SyndEntry::getLink); //links
        entryExt.put("description", e -> e.getDescription() != null ? e.getDescription().getValue() : null);
        entryExt.put(
            "contents",
            $ -> $.getContents().stream().map(SyndContent::getValue).collect(Collectors.joining("; "))
        );
        entryExt.put(
            "enclosures",
            $ -> $.getEnclosures().stream().map(SyndEnclosure::getUrl).collect(Collectors.joining("; "))
        );
        entryExt.put("publishedDate", s -> s.getPublishedDate() != null ? s.getPublishedDate().toString() : null);
        entryExt.put("updatedDate", s -> s.getUpdatedDate() != null ? s.getUpdatedDate().toString() : null);
        entryExt.put("author", SyndEntry::getAuthor); // authors
        entryExt.put(
            "contributors",
            $ -> $.getContributors().stream().map(SyndPerson::getName).collect(Collectors.joining("; "))
        );
        entryExt.put(
            "categories",
            $ -> $.getCategories().stream().map(c -> c.getName()).collect(Collectors.joining("; "))
        );
        entryExt.put("comments", SyndEntry::getComments); // authors
    }
}
