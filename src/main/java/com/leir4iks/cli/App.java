package com.leir4iks.cli;

import java.util.List;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Unmatched;

@Command(
        name = "unarchiver",
        mixinStandardHelpOptions = true,
        version = "Unarchiver 1.0",
        description = "A powerful command-line archive extractor.",
        subcommands = {
                ExtractCommand.class,
                ListCommand.class
        }
)
public class App {

        @Unmatched
        private List<String> unmatched;

}