package com.leir4iks.extraction;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RarExtraction implements Extraction {

    @Override
    public void extract(Path sourceFile, Path destinationDirectory) throws IOException {
        try (Archive archive = new Archive(sourceFile.toFile())) {
            FileHeader fileHeader;
            while ((fileHeader = archive.nextFileHeader()) != null) {
                if (!fileHeader.isDirectory()) {
                    Path targetPath = destinationDirectory.resolve(fileHeader.getFileName()).normalize();
                    if (!targetPath.startsWith(destinationDirectory)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + fileHeader.getFileName());
                    }

                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                        archive.extractFile(fileHeader, fos);
                    }
                }
            }
        } catch (RarException e) {
            throw new IOException("Error extracting RAR archive: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> list(Path sourceFile) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (Archive archive = new Archive(sourceFile.toFile())) {
            FileHeader fileHeader;
            while ((fileHeader = archive.nextFileHeader()) != null) {
                fileNames.add(fileHeader.getFileName());
            }
        } catch (RarException e) {
            throw new IOException("Error reading RAR archive: " + e.getMessage(), e);
        }
        return fileNames;
    }
}