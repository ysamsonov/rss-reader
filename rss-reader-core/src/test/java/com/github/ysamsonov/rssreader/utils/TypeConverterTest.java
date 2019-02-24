package com.github.ysamsonov.rssreader.utils;

import com.github.ysamsonov.rssreader.exception.TypeConvertException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
class TypeConverterTest {

    static Object[][] dataProvider() {
        return new Object[][]{
            {"10", Integer.class, 10},
            {"-1210", Integer.class, -1210},
            {"false", Boolean.class, false},
            {"true", Boolean.class, true},
            {"35", Double.class, 35.0},
            {null, Double.class, null},
            {44, Integer.class, 44},
        };
    }


    @MethodSource("dataProvider")
    @ParameterizedTest
    void convert(Object val, Class<?> targetType, Object converted) {
        assertThat(TypeConverter.convert(targetType, val)).isEqualTo(converted);
    }

    @Test
    @SuppressWarnings("NumberEquality")
    void sameInstance() {
        Integer instance = 543;
        assertThat(TypeConverter.convert(Integer.class, instance) == instance).isTrue();
    }

    @Test
    void nullType() {
        Assertions.assertThrows(TypeConvertException.class, () -> TypeConverter.convert(null, 42));
    }
}