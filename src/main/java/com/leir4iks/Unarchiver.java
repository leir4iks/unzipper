package com.leir4iks;

import com.leir4iks.cli.ListCommand;
import com.leir4iks.cli.UnzipCommand;
import picocli.CommandLine;
import java.util.Scanner;

public class Unarchiver {
    public static void main(String[] args) {
        System.out.println("Unzipper is ready. Type 'unzip <file>', 'list <file>', or 'stop'.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("stop")) {
                    System.out.println("Stopping...");
                    break;
                }

                String[] parts = line.split("\\s+");
                String command = parts[0].toLowerCase();

                Runnable commandToRun;
                if ("unzip".equals(command)) {
                    commandToRun = new UnzipCommand();
                } else if ("list".equals(command)) {
                    commandToRun = new ListCommand();
                } else {
                    System.out.println("Unknown command: " + command);
                    continue;
                }

                String[] commandArgs = new String[parts.length - 1];
                System.arraycopy(parts, 1, commandArgs, 0, commandArgs.length);

                new CommandLine(commandToRun).execute(commandArgs);
            }
        }
    }
}