package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.exception.RssReaderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class ParsersTest {

    static Object[][] fieldsDataProvider() {
        return new Object[][]{
            {"", new String[]{}},
            {"all", new String[]{}},
            {"  title   description  ", new String[]{"title", "description"}},
        };
    }

    @ParameterizedTest
    @MethodSource("fieldsDataProvider")
    void fields(String fields, String[] expected) {
        assertThat(Parsers.fields().apply(fields)).containsExactlyInAnyOrder(expected);
    }

    @Test
    void booleanVal() {
        assertThat(Parsers.booleanVal().apply("y")).isTrue();
        assertThat(Parsers.booleanVal().apply("yes")).isTrue();
        assertThat(Parsers.booleanVal().apply("n")).isFalse();
        assertThat(Parsers.booleanVal().apply("no")).isFalse();

        Assertions.assertThrows(RssReaderException.class, () -> Parsers.booleanVal().apply("unk"));
    }

    @Test
    void feedNumber() {
        assertThat(Parsers.feedNumber().apply("10")).isEqualTo(9);
        assertThat(Parsers.feedNumber().apply("54")).isEqualTo(53);
    }
}