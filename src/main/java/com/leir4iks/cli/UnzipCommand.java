package com.leir4iks.cli;

import com.leir4iks.extraction.Extraction;
import com.leir4iks.extraction.ExtractionFactory;
import com.leir4iks.localization.Messages;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = "unzip",
        mixinStandardHelpOptions = true,
        description = "Extracts files from one or more archives."
)
public class UnzipCommand implements Runnable {

    @Parameters(index = "0..*", description = "The archive file(s) to extract.")
    private List<Path> archivePaths;

    @Parameters(index = "1..*", arity = "0..1", description = "The optional destination directory.")
    private Path destinationPath;

    @Override
    public void run() {
        if (archivePaths == null || archivePaths.isEmpty()) {
            System.err.print(Messages.ERROR_NO_ARCHIVES_SPECIFIED);
            return;
        }

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);

        for (Path archivePath : archivePaths) {
            Runnable task = () -> processArchive(archivePath);
            executor.submit(task);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.print(Messages.SUCCESS_ALL_TASKS_COMPLETED);
    }

    private void processArchive(Path archivePath) {
        try {
            Path source = archivePath.toAbsolutePath();
            if (!Files.exists(source)) {
                System.err.printf(Messages.ERROR_FILE_NOT_FOUND, source);
                return;
            }

            Path outputDir = (destinationPath != null) ? destinationPath.toAbsolutePath() : source.getParent();
            if (outputDir == null) {
                outputDir = Path.of(".").toAbsolutePath();
            }

            Files.createDirectories(outputDir);
            Optional<Extraction> extractionOpt = ExtractionFactory.getExtraction(source);

            if (extractionOpt.isEmpty()) {
                System.err.print(Messages.ERROR_UNSUPPORTED_FORMAT);
                return;
            }

            System.out.printf(Messages.INFO_EXTRACTION_STARTING, source.getFileName(), Thread.currentThread().getName());
            Extraction extraction = extractionOpt.get();
            extraction.extract(source, outputDir);
            System.out.printf(Messages.SUCCESS_EXTRACTION_SINGLE, source.getFileName(), outputDir);

        } catch (IOException e) {
            System.err.printf(Messages.ERROR_EXTRACTION_FAILED, e.getMessage());
        } catch (Exception e) {
            System.err.printf(Messages.ERROR_TASK_FAILED, archivePath.getFileName(), e.getMessage());
        }
    }
}