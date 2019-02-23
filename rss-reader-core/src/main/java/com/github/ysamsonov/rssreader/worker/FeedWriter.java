package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 2019-02-23
 */
@Slf4j
public class FeedWriter {

    private final FeedConfig feedConfig;

    public FeedWriter(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
    }

    @SneakyThrows
    public void write(SyndFeed syndFeed) {
        log.info("Write data from feed '{}' to file '{}'", feedConfig.getUrl(), feedConfig.getFileName());
        try (
            FileWriter fw = new FileWriter(feedConfig.getFileName(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)
        ) {
            try {
                for (SyndEntry entry : syndFeed.getEntries()) {
                    writeEntry(out, entry);
                }
            }
            catch (Exception e) {
                // TODO bad idea
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private void writeEntry(PrintWriter out, SyndEntry entry) {
        for (var extr : FieldExtractors.entryExt.entrySet()) {
            out.write(extr.getKey() + ": ");

            Object value = extr.getValue().apply(entry);
            if (value != null) {
                out.write(value.toString());
            }
            out.write("\n");
        }
    }
}
