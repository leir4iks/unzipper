package com.leir4iks;

import picocli.CommandLine;

public class Unarchiver {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}