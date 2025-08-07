package com.leir4iks.extraction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class SevenZipExtraction implements Extraction {

    @Override
    public void extract(Path sourceFile, Path destinationDirectory) throws IOException {
        byte[] buffer = new byte[8192];
        try (SevenZFile sevenZFile = new SevenZFile(sourceFile.toFile())) {
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    Path targetPath = destinationDirectory.resolve(entry.getName()).normalize();
                    if (!targetPath.startsWith(destinationDirectory)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + entry.getName());
                    }

                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                        int len;
                        while ((len = sevenZFile.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> list(Path sourceFile) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (SevenZFile sevenZFile = new SevenZFile(sourceFile.toFile())) {
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                fileNames.add(entry.getName());
            }
        }
        return fileNames;
    }
}