package com.github.ysamsonov.rssreader.cli;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Getter
@AllArgsConstructor
public class Command {

    private final String name;

    private final Action action;

    public interface Action {
        void exec();
    }
}
