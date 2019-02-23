package com.github.ysamsonov.rssreader.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public class ConfigurationManager {

    private final ObjectMapper mapper = new ObjectMapper()
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        .enable(JsonParser.Feature.ALLOW_COMMENTS)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final File configFile;

    @Getter
    private ReaderConfig config;

    public ConfigurationManager(File configFile) {
        this.configFile = configFile;

        load();
    }

    @SneakyThrows
    private void load() {
        if (configFile.exists()) {
            this.config = mapper.readValue(configFile, ReaderConfig.class);
        }
        else {
            this.config = new ReaderConfig();
        }
        System.out.println();
    }
}
