package com.leir4iks.cli;

import com.leir4iks.extraction.Extraction;
import com.leir4iks.extraction.ExtractionFactory;
import com.leir4iks.localization.Messages;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = "list",
        mixinStandardHelpOptions = true,
        description = "Lists the contents of an archive without extracting."
)
public class ListCommand implements Runnable {

    @Parameters(index = "0", description = "The archive file to inspect.")
    private Path archivePath;

    @Override
    public void run() {
        try {
            Path source = archivePath.toAbsolutePath();
            if (!Files.exists(source)) {
                System.err.printf(Messages.ERROR_FILE_NOT_FOUND, source);
                return;
            }

            Optional<Extraction> extractionOpt = ExtractionFactory.getExtraction(source);
            if (extractionOpt.isEmpty()) {
                System.err.print(Messages.ERROR_UNSUPPORTED_FORMAT);
                return;
            }

            System.out.printf(Messages.INFO_LISTING_CONTENTS, source.getFileName());
            Extraction extraction = extractionOpt.get();
            List<String> files = extraction.list(source);
            files.forEach(System.out::println);

        } catch (Exception e) {
            System.err.printf(Messages.ERROR_LISTING_FAILED, e.getMessage());
        }
    }
}