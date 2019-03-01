package com.github.ysamsonov.rssreader.cli.action;

import com.github.ysamsonov.rssreader.cli.Command;
import com.github.ysamsonov.rssreader.exception.InterruptCliActionException;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Abstract class contains common methods for console actions.
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
@RequiredArgsConstructor
abstract class BaseConsoleAction implements Command.Action {

    private static final String EXIT_CMD = "$exit";

    final Scanner scanner;

    /**
     * @see this#read(String, Function, Predicate, Object)
     */
    <T> T read(
        String msg,
        Function<String, T> converter,
        Predicate<T> validator
    ) {
        return read(msg, converter, validator, null);
    }

    /**
     * Reads with verification and try again if necessary.
     *
     * @param msg           - message to show user before action
     * @param converter     - convert given string from console to type
     * @param validator     - validate input data
     * @param fallbackValue - fall back value, returns if input is empty and if fallbackValue is not null
     * @return converter and validate value
     */
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

            if (line.equalsIgnoreCase(EXIT_CMD)) {
                throw new InterruptCliActionException();
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
            System.out.println(String.format(
                "Incorrect format of value. Please repeat. Or interrupt the operation by typing '%s'",
                EXIT_CMD
            ));
        }
    }
}
