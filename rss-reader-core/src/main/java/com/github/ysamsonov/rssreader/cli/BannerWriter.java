package com.github.ysamsonov.rssreader.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
class BannerWriter {

    private static final String location = "banner.txt";

    void write() {
        var stream = BannerWriter.class.getClassLoader().getResourceAsStream(location);
        if (stream == null) {
            System.out.println("RSS Reader");
            return;
        }

        try (
            stream;
            var reader = new InputStreamReader(stream);
            var buffReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = buffReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.print("\n\n\n");
        }
        catch (IOException ignore) {
            System.out.println("RSS Reader");
        }
    }
}
