package com.github.ysamsonov.rssreader.worker;

import com.github.ysamsonov.rssreader.exception.RssReaderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
class DelayParserTest {

    static Object[][] positiveDataProvider() {
        return new Object[][]{
            {"10s", DelayParser.Delay.of(10, TimeUnit.SECONDS)},
            {"12ms", DelayParser.Delay.of(12, TimeUnit.MILLISECONDS)},
            {"21m", DelayParser.Delay.of(21, TimeUnit.MINUTES)},
            {"12h", DelayParser.Delay.of(12, TimeUnit.HOURS)},
            {"13d", DelayParser.Delay.of(13, TimeUnit.DAYS)},
        };
    }

    static Object[][] negativeDataProvider() {
        return new Object[][]{
            {"110ms", false},
            {"101s", false},
            {"110m", false},
            {"110h", false},
            {"101d", false},

            {"10ss", true},
            {"-110h", true},
            {"-110D", true},
            {"-110D", true},
            {"-110d", true},
        };
    }


    @MethodSource("positiveDataProvider")
    @ParameterizedTest
    void positive(String val, DelayParser.Delay res) {
        assertThat(new DelayParser().parse(val)).isEqualTo(res);
    }

    @MethodSource("negativeDataProvider")
    @ParameterizedTest
    void negative(String val, boolean hasException) {
        Executable executable = () -> new DelayParser().parse(val);
        if (hasException) {
            Assertions.assertThrows(RssReaderException.class, executable);
        }
        else {
            Assertions.assertDoesNotThrow(executable);
        }
    }
}