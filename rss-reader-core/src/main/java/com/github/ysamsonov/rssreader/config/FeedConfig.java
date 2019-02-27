package com.github.ysamsonov.rssreader.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ysamsonov.rssreader.constants.DateFormats;
import com.github.ysamsonov.rssreader.utils.MiscUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Concrete RSS or Atom feed configuration
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Getter
@Setter
@Accessors(chain = true)
public class FeedConfig {

    /**
     * Url to download information
     */
    private String url;

    /**
     * Synchronization activity flag
     */
    private boolean enabled = true;

    /**
     * Target file name
     */
    private String fileName;

    /**
     * List of fields for translation, empty array for translation of all fields
     */
    private Collection<String> fields = new ArrayList<>();

    /**
     * Feed pooling interval
     */
    private String fetchTime;

    /**
     * Count of items for pooling
     */
    private int fetchCount = 10;

    /**
     * Date of last pooling
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormats.DATE__TIME__TZ_ISO_8601)
    private Date lastFetchDate;

    public FeedConfig() {
    }

    @SuppressWarnings("WeakerAccess")
    public void invertState() {
        this.enabled = !this.enabled;
    }

    /**
     * Construct predicate to check field on write step
     *
     * @return predicate
     */
    public Predicate<String> fieldPredicate() {
        if (MiscUtils.isNullOrEmpty(getFields())) {
            return $ -> true;
        }
        else {
            return $ -> getFields().contains($);
        }
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
