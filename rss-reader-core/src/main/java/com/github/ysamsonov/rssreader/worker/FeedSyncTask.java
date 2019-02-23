package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.config.FeedConfig;
import com.github.ysamsonov.rssreader.helpers.FieldExtractors;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class FeedSyncTask implements Runnable {

    private final FeedConfig feedConfig;

    private final URL url;

    // TODO куда писать надо принимать снаружи, а снаружи ииметь холдер стримов

    @SneakyThrows
    public FeedSyncTask(FeedConfig feedConfig) {
        this.feedConfig = feedConfig;
        this.url = new URL(feedConfig.getUrl());
    }

    @Override
    public void run() {
        System.out.println("!!!!! run -> " + feedConfig.getUrl());
        SyndFeed syndFeed = loadData();
        writeFeed(syndFeed);
    }

    @SneakyThrows
    private void writeFeed(SyndFeed syndFeed) {
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

    @SneakyThrows
    private SyndFeed loadData() {
        var xmlReader = new XmlReader(url);
        var feedInput = new SyndFeedInput();
        var feed = feedInput.build(xmlReader);
        return feed;
    }
}
