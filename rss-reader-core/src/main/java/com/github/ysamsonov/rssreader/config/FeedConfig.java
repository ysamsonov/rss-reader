package com.github.ysamsonov.rssreader.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Getter
@Setter
@Accessors(chain = true)
public class FeedConfig {

    private String url;

    private boolean enabled = true;

    private String fileName;

    private Collection<String> fields = new ArrayList<>();

    private Date lastFetchDate;

    public FeedConfig() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FeedConfig)) {
            return false;
        }

        FeedConfig that = (FeedConfig) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
