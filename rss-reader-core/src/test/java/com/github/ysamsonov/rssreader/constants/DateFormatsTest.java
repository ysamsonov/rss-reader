package com.github.ysamsonov.rssreader.constants;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class DateFormatsTest {

    @Test
    void iso8601Test() throws ParseException {
        var sdf = new SimpleDateFormat(DateFormats.DATE__TIME__TZ_ISO_8601);
        var original = new Date();
        var convertedStr = sdf.format(original);
        var parsed = sdf.parse(convertedStr);

        assertThat(parsed).isEqualTo(original);
    }
}