package com.github.ysamsonov.rssreader.junit;

import lombok.Getter;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
public class TempFileExtension implements BeforeEachCallback, AfterEachCallback {

    @Getter
    private File tmpFile;

    @Override
    public void afterEach(ExtensionContext context) {
        if (!tmpFile.delete()) {
            tmpFile.deleteOnExit();
        }
        tmpFile = null;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        tmpFile = File.createTempFile("JUnit5Test", ".tmp");
    }
}
