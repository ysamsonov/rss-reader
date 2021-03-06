package com.github.ysamsonov.rssreader.cli;

import com.github.ysamsonov.rssreader.exception.InterruptCliActionException;
import com.github.ysamsonov.rssreader.exception.InterruptMenuException;
import com.github.ysamsonov.rssreader.utils.MiscUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Component represent menu for cli
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-23
 */
@Slf4j
@SuppressWarnings("WeakerAccess")
@AllArgsConstructor
public class Menu {

    private final Scanner scanner;

    private final String title;

    private final List<Command> commands;

    /**
     * Print menu to console and handle users action
     */
    public void show() {
        showMenu();
        try {
            awaitAction();
        }
        catch (NoSuchElementException noSuchEl) {
            throw new InterruptMenuException();
        }
        catch (Exception e) {
            System.out.println(String.format(
                "Error during execute command! Error: %s. Try again.\n\n",
                e.getMessage()
            ));
        }
    }

    /**
     * Print menu
     */
    private void showMenu() {
        System.out.println(title);
        for (int i = 0; i < commands.size(); i++) {
            System.out.println(String.format("[%d] %s", i + 1, commands.get(i).getName()));
        }
        System.out.println("Please enter command and press Enter: ");
    }

    /**
     * Handle user action with small validation
     */
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


            try {
                Command command = commands.get(commandNum);
                command
                    .getAction()
                    .exec();
            }
            catch (InterruptCliActionException e) {
                log.info("Interrupt CLI action. {}", e.getMessage());
                return;
            }
            return;
        }
    }
}
