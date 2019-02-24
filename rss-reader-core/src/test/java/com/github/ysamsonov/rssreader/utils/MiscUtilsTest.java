package com.github.ysamsonov.rssreader.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
class MiscUtilsTest {

    static Object[][] isNumericDataProvider() {
        return new Object[][]{
            {"", false},
            {"ada", false},
            {"10", true},
            {"-121", true},
            {"-121da", false},
        };
    }


    @MethodSource("isNumericDataProvider")
    @ParameterizedTest
    void isNumeric(String val, boolean res) {
        assertThat(MiscUtils.isNumeric(val)).isEqualTo(res);
    }

    @Test
    void isNullOrEmptyString() {
        assertThat(MiscUtils.isNullOrEmpty((String) null)).isTrue();
        assertThat(MiscUtils.isNullOrEmpty("")).isTrue();
        assertThat(MiscUtils.isNullOrEmpty("32")).isFalse();
    }

    @Test
    void isNullOrEmptyCollection() {
        assertThat(MiscUtils.isNullOrEmpty((Collection<?>) null)).isTrue();
        assertThat(MiscUtils.isNullOrEmpty(Collections.emptyList())).isTrue();
        assertThat(MiscUtils.isNullOrEmpty(Collections.singleton("32"))).isFalse();
    }
}