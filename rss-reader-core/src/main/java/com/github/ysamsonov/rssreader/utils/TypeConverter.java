package com.github.ysamsonov.rssreader.utils;

import com.github.ysamsonov.rssreader.constants.DateFormats;
import com.github.ysamsonov.rssreader.exception.TypeConvertException;
import lombok.AllArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A set of type converters
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
public final class TypeConverter {

    private static final Collection<Converter> converters = Arrays.asList(
        new Converter<>(equalTo(String.class), equalTo(Boolean.class), Boolean::parseBoolean),
        new Converter<>(equalTo(String.class), equalTo(Short.class), (Function<String, Short>) Short::parseShort),
        new Converter<>(equalTo(String.class), equalTo(Byte.class), (Function<String, Byte>) Byte::parseByte),
        new Converter<>(equalTo(String.class), equalTo(Integer.class), (Function<String, Integer>) Integer::parseInt),
        new Converter<>(equalTo(String.class), equalTo(Long.class), (Function<String, Long>) Long::parseLong),
        new Converter<>(equalTo(String.class), equalTo(Double.class), Double::parseDouble),
        new Converter<>(equalTo(String.class), equalTo(Float.class), Float::parseFloat),
        new Converter<>(equalTo(String.class), equalTo(Date.class), TypeConverter::parseDate)
    );

    /**
     * Convert data to the given type
     *
     * @param targetType - type of valueo to convert
     * @param value      - value to convert
     * @return - converted value
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<T> targetType, Object value) {
        if (targetType == null) {
            throw new TypeConvertException("Unknown target type for value '%s'", value);
        }

        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return (T) value;
        }

        final Class<?> sourceType = value.getClass();

        for (Converter converter : converters) {
            if (converter.matches(sourceType, targetType)) {
                return (T) converter.convert(value);
            }
        }

        throw new TypeConvertException(
            "Unable to convert value %s from type %s to type %s",
            value, sourceType, targetType
        );
    }

    private static <T> Predicate<T> equalTo(T expected) {
        return $ -> Objects.equals($, expected);
    }

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat(DateFormats.DATE__TIME__TZ_ISO_8601).parse(date);
        }
        catch (ParseException e) {
            throw new TypeConvertException("Unable to parse date %s", date);
        }
    }

    @AllArgsConstructor
    private static class Converter<FROM, TO> {

        private final Predicate<Class> fromPredicate;

        private final Predicate<Class> toPredicate;

        private final Function<FROM, TO> converter;

        TO convert(FROM val) {
            return converter.apply(val);
        }

        boolean matches(Class fromCandidate, Class toCandidate) {
            return fromPredicate.test(fromCandidate) && toPredicate.test(toCandidate);
        }
    }
}
