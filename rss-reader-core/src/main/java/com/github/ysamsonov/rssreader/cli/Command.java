package com.github.ysamsonov.rssreader.cli;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represent menu command for {@link Menu}
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Getter
@AllArgsConstructor
public class Command {

    /**
     * Display name for command
     */
    private final String name;

    /**
     * Action on activate command
     */
    private final Action action;

    /**
     * Interface for all console action
     */
    public interface Action {
        void exec();
    }
}
