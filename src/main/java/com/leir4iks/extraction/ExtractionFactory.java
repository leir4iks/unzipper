package com.leir4iks.extraction;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ExtractionFactory {

    private static final Map<String, Supplier<Extraction>> EXTRACTION_MAP = Map.of(
            ".zip", ZipExtraction::new,
            ".rar", RarExtraction::new,
            ".7z", SevenZipExtraction::new,
            ".tar.gz", TarGzExtraction::new,
            ".tar.bz2", TarBz2Extraction::new
    );

    public static Optional<Extraction> getExtraction(Path archivePath) {
        String fileName = archivePath.getFileName().toString().toLowerCase();

        return EXTRACTION_MAP.entrySet().stream()
                .filter(entry -> fileName.endsWith(entry.getKey()))
                .findFirst()
                .map(entry -> entry.getValue().get());
    }
}