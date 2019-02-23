package com.github.ysamsonov.rssreader.helpers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public final class FieldExtractors {

    public static final HashMap<String, Function<SyndFeed, ?>> feedExt = new HashMap<>();

    public static final HashMap<String, Function<SyndEntry, ?>> entryExt = new HashMap<>();

    static {
        feedExt.put("copyright", SyndFeed::getCopyright);
        feedExt.put("generator", SyndFeed::getGenerator);
        feedExt.put("image", f -> f.getImage() != null ? f.getImage().getUrl() : null);
        feedExt.put("icon", f -> f.getIcon() != null ? f.getIcon().getUrl() : null);

        entryExt.put("uri", SyndEntry::getUri);
        entryExt.put("title", SyndEntry::getTitle);
        entryExt.put("link", SyndEntry::getLink); //links
        entryExt.put("description", e -> e.getDescription() != null ? e.getDescription().getValue() : null);
        entryExt.put("contents", SyndEntry::getContents);
//        entryExt.put("contents", e->e.getContents().stream().map(SyndContent::getValue).collect(Collectors.joining()));
        entryExt.put("enclosures", SyndEntry::getEnclosures);
        entryExt.put("publishedDate", SyndEntry::getPublishedDate);
        entryExt.put("updatedDate", SyndEntry::getUpdatedDate);
        entryExt.put("author", SyndEntry::getAuthor); // authors
        entryExt.put("contributors", SyndEntry::getContributors); // authors
        entryExt.put("categories", SyndEntry::getCategories); // authors
        entryExt.put("comments", SyndEntry::getComments); // authors
    }
}
