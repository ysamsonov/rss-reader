package com.github.ysamsonov.rssreader.cli;

import com.github.ysamsonov.rssreader.utils.MiscUtils;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Scanner;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@AllArgsConstructor
public class Menu {

    private final Scanner scanner;

    private final String title;

    private final List<Command> commands;

    public void show() {
        showMenu();
        try {
            awaitAction();
        }
        catch (Exception ignore) {
            // something went wrong, repeat awaiting
            System.out.println("Error during execute command!\n\n");
            //TODO : надо чтото сделать так не круто))
        }
    }

    private void showMenu() {
        System.out.println(title);
        for (int i = 0; i < commands.size(); i++) {
            System.out.println(String.format("[%d] %s", i + 1, commands.get(i).getName()));
        }
        System.out.println("Please enter command and press Enter: ");
    }

    private void awaitAction() {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!MiscUtils.isNumeric(line)) {
                System.out.println("Incorrect command!");
                continue;
            }

            int commandNum = Integer.parseInt(line) - 1;
            if (commandNum > commands.size()) {
                System.out.println("Incorrect command!");
                continue;
            }

            commands.get(commandNum)
                .getAction()
                .exec();
            return;
        }
    }
}
