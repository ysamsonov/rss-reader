package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.cli.Command;
import com.github.ysamsonov.rssreader.exception.RssReaderException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@RequiredArgsConstructor
abstract class BaseConsoleAction implements Command.Action {

    final Scanner scanner;

    <T> T read(
        String msg,
        Function<String, T> converter,
        Predicate<T> validator
    ) {
        return read(msg, converter, validator, null);
    }

    <T> T read(
        String msg,
        Function<String, T> converter,
        Predicate<T> validator,
        T fallbackValue
    ) {
        System.out.println(msg);

        T val;
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() && fallbackValue != null) {
                return fallbackValue;
            }

            try {
                val = converter.apply(line);
            }
            catch (Exception e) {
                System.out.println("Incorrect format of value. Please repeat.");
                continue;
            }

            if (validator.test(val)) {
                return val;
            }
            System.out.println("Incorrect format of value. Please repeat.");
        }
    }

    boolean parseBoolean(String val) {
        if (Objects.equals(val, "y") || Objects.equals(val, "yes")) {
            return true;
        }

        if (Objects.equals(val, "n") || Objects.equals(val, "no")) {
            return false;
        }

        throw new RssReaderException("Unknown value '%s'", val);
    }
}
