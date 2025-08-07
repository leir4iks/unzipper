package com.leir4iks.extraction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtraction implements Extraction {

    @Override
    public void extract(Path sourceFile, Path destinationDirectory) throws IOException {
        byte[] buffer = new byte[2048];
        try (FileInputStream fis = new FileInputStream(sourceFile.toFile());
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    Path targetPath = destinationDirectory.resolve(zipEntry.getName()).normalize();
                    if (!targetPath.startsWith(destinationDirectory)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + zipEntry.getName());
                    }
                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }
    }

    @Override
    public List<String> list(Path sourceFile) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(sourceFile.toFile());
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                fileNames.add(zipEntry.getName());
                zis.closeEntry();
            }
        }
        return fileNames;
    }
}